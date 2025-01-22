package com.vodafone.orderapi.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ExtractClientDataFromPostgres {

    @JobWorker(type = "dbresultset",autoComplete = false)
    public Map<String,Boolean> getClientData(final JobClient jobClient, final ActivatedJob activatedJob) throws JsonProcessingException {

        System.out.println(activatedJob.getVariables());
        String response=activatedJob.getVariables();
        try {
            // Step 1: Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Step 2: Parse JSON string into JsonNode
            JsonNode rootNode = objectMapper.readTree(response);

            // Step 3: Access "myclient" object
            JsonNode myClientNode = rootNode.get("myclient");

            // Step 4: Access "resultSet" array
            JsonNode resultSetNode = myClientNode.get("resultSet");

            // Step 5: Iterate through the array and extract "id"
            if (resultSetNode.isArray()) {
                for (JsonNode item : resultSetNode) {
                    String id = item.get("id").asText();
                    System.out.println("ID: " + id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        Map<String, Boolean> map=new HashMap<>();
        map.put("dbStatus",true);




        jobClient.newCompleteCommand(activatedJob.getKey())
                .variables(map)
                .send()
                .exceptionally((throwable)->{
                    throw new RuntimeException("Job not found");
                });
return map;
    }

}
