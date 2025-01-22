package com.vodafone.orderapi.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.orderapi.models.Order;
import com.vodafone.orderapi.models.OrderStatus;
import com.vodafone.orderapi.services.OrderDao;
import com.vodafone.orderapi.services.OrderImpl;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class CancelOrderJobConfiguration {

    @Autowired
    private OrderDao orderDao;
    @JobWorker(type = "cancelorder", autoComplete = false)
    public Map<String,Order> processPayment(final JobClient jobClient, final ActivatedJob activatedJob) throws JsonProcessingException {

        Map<String,Object> receivedData=activatedJob.getVariablesAsMap();
        receivedData.entrySet().stream().forEach(entrySet->{
            log.info(entrySet.getKey()+","+entrySet.getValue());
        });

        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        Map<String, Object> item = (Map<String, Object>) variables.get("order");

        long orderId=Long.parseLong(item.get("orderId").toString());
         Order order=orderDao.updateOrderStatus(orderId, OrderStatus.Cancelled);

         Map<String,Order> orderMap=new HashMap<>();
         orderMap.put("orderCancelled",order);

        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(orderMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return orderMap;


    }


}
