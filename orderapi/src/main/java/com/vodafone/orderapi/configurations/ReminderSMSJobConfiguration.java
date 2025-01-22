package com.vodafone.orderapi.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class ReminderSMSJobConfiguration {
    @Autowired
    private ZeebeClient zeebeClient;
    private static int count=1;

    @JobWorker(type = "sendreminder", autoComplete = false)
    public Map<String,Boolean> reminderSMS(final JobClient jobClient, final ActivatedJob activatedJob) throws JsonProcessingException {


         Map<String,Boolean> orderMap=new HashMap<>();
         orderMap.put("deliveryStatus",false);
         log.info("SMS Sent="+count);
         count++;
        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(orderMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return orderMap;


    }


}
