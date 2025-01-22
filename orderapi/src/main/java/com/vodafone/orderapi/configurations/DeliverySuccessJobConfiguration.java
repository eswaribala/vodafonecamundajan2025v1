package com.vodafone.orderapi.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vodafone.orderapi.services.OrderDao;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DeliverySuccessJobConfiguration {
    @Autowired
    private ZeebeClient zeebeClient;


    @JobWorker(type = "deliverysuccessupdate", autoComplete = false)
    public Map<String,Boolean> deliverySuccess(final JobClient jobClient, final ActivatedJob activatedJob) throws JsonProcessingException {


         Map<String,Boolean> orderMap=new HashMap<>();
         orderMap.put("deliveryStatus",true);
        zeebeClient.newPublishMessageCommand()
                .messageName("Message_Delivery_Status")
                .correlationKey("2002")

                // .variables(subProcess)
                .timeToLive(Duration.ofMinutes(3))
                .send()

                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(orderMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return orderMap;


    }


}
