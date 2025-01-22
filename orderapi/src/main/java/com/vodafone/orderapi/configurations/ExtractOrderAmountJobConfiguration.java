package com.vodafone.orderapi.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.orderapi.models.*;
import com.vodafone.orderapi.services.ProductDao;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Configuration
public class ExtractOrderAmountJobConfiguration {

    @Autowired
    private ProductDao productDao;
    @JobWorker(type = "extractorderamount",autoComplete = false)
    public Map<String, String> extractOrderAmount(final JobClient jobClient, final ActivatedJob activatedJob) throws JsonProcessingException {

        ObjectMapper objectMapper=new ObjectMapper();
        String variablesJson = activatedJob.getVariables();

        // Deserialize variables to a Map
        Map<String, Object> variables = objectMapper.readValue(variablesJson, new TypeReference<>() {});

        // Read the "items" variable (array of JSON objects)
        Map<String, Object> item = (Map<String, Object>) variables.get("order");

        long orderAmount=Long.parseLong(item.get("orderAmount").toString());

        long orderId=Long.parseLong(item.get("orderId").toString());
          List<Product> productList=this.productDao.getProductByOrderId(orderId);

        Map<String,String> orderMap=new HashMap<>();
        orderMap.put("orderAmountCaptured",String.valueOf(orderAmount));
        orderMap.put("productName",productList.get(0).getName());
        orderMap.put("season",getRandomEnumValue(SeasonType.class).toString());
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(orderMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return orderMap;
    }

    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass){
        T[] enumValues=enumClass.getEnumConstants();
        return enumValues[new Random().nextInt(enumValues.length)];
    }
}
