package com.vodafone.deliveryapi.controllers;

import com.vodafone.deliveryapi.configurations.ProcessConstant;
import com.vodafone.deliveryapi.dtos.GenericResponse;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("kafkaconsumers")
public class KafkaProcessController {

    @Autowired
    private ZeebeClient zeebeClient;

    //instantiate the bpmn
    @GetMapping("/v1.0")
    ResponseEntity<?> startDBProcess(){

        Map<String,String> map=new HashMap<>();
         map.put("name","headphone");
         this.zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstant.KAFKA_CONSUMER_BPMN_Process_Constant)
                .latestVersion()
                .variables(map)
                .send();

        //System.out.println(processInstanceEvent.getProcessInstanceKey());


        return ResponseEntity.status(HttpStatus.CREATED).body("Instance created for"+ProcessConstant.KAFKA_CONSUMER_BPMN_Process_Constant);

    }

}
