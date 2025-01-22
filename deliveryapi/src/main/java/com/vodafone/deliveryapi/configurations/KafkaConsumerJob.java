package com.vodafone.deliveryapi.configurations;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerJob {


    @JobWorker(type = "kafkaconsumerjob",autoComplete = false)
    public Map<String,String> consumeData(final JobClient jobClient, final ActivatedJob activatedJob){
        log.info("kafka consumption triggered");
        Map<String,String> notificationMap=new HashMap<>();
        notificationMap.put("kafkastatus","active");
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
