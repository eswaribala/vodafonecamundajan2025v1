package com.vodafone.orderapi.configurations;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class OrderCancellationJobNotification {

    @JobWorker(type = "notifycancelorder",autoComplete = false)
    public Map<String, Boolean> NotifyCustomerOnOrderCancellation(final JobClient jobClient, final ActivatedJob activatedJob){

        log.info("Order Cancelled");
        Map<String,Boolean> notificationMap=new HashMap<>();
        notificationMap.put("notificationStatus",true);
        //pub sub with bpmn error
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(notificationMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return notificationMap;
    }
}
