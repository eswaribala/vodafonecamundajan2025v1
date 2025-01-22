package com.vodafone.orderapi.configurations;

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
public class PaymentJobConfiguration {

    @Autowired
    private ZeebeClient zeebeClient;
    @JobWorker(type = "paymentgateway", autoComplete = false)
    public Map<String,Boolean> processPayment(final JobClient jobClient, final ActivatedJob activatedJob){

        Map<String,Object> receivedData=activatedJob.getVariablesAsMap();
        receivedData.entrySet().stream().forEach(entrySet->{
            log.info(entrySet.getKey()+","+entrySet.getValue());
        });

        Map<String,Boolean> paymentStatusMap=new HashMap<>();

        if(receivedData.get("paymentMethod").toString().equals("Credit Card")) {
            paymentStatusMap.put("paymentStatus", true);

            zeebeClient.newPublishMessageCommand()
                    .messageName("Message_Delivery_Start")
                    .correlationKey("2001")

                   // .variables(subProcess)
                    .timeToLive(Duration.ofMinutes(3))
                    .send()

                    .exceptionally((throwable)->{
                        throw new RuntimeException("Job not found");
                    });

        }
        else
            paymentStatusMap.put("paymentStatus",false);

        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(paymentStatusMap)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
        return paymentStatusMap;


    }


}
