package com.vodafone.orderapi;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.DeploymentAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@ZeebeProcessTest
class OrderapiApplicationTests {


    private ZeebeClient client;
    private RecordStream recordStream;



    @BeforeEach
    public void testDeployment() {
        DeploymentEvent event = client.newDeployResourceCommand()
                .addResourceFromClasspath("processes/stockprocess.bpmn")
                .send()
                .join();
        DeploymentAssert assertions = BpmnAssert.assertThat(event);
    }

    @Test
    public void testProcessInstance() {

        ProcessInstanceEvent event = client.newCreateInstanceCommand()
                .bpmnProcessId("Process_Stock")
                .latestVersion()
                .send()
                .join();
        ProcessInstanceAssert assertions = BpmnAssert.assertThat(event);
    }


    @Test
    public void testProcessInstanceEvent() {
        ProcessInstanceEvent event = client.newCreateInstanceCommand()
                .bpmnProcessId("Process_Stock")
                .latestVersion()

                .send()
                .join();
        ProcessInstanceAssert assertions = BpmnAssert.assertThat(event);
    }

    @Test
    public void testActivatedJob(){

        ActivateJobsResponse response = client.newActivateJobsCommand()
                .jobType("stockcheck")
                .maxJobsToActivate(1)
                .send()
                .join();
        if(!response.getJobs().isEmpty()) {
            List<ActivatedJob> activatedJob = response.getJobs();
            BpmnAssert.assertThat(activatedJob.get(0));
        }
    }


    /*@Test
    public void testProcessInstanceEventResult() {
        ProcessInstanceResult event = client.newCreateInstanceCommand()
                .bpmnProcessId("Process_Stock")
                .latestVersion()
                .withResult()
                .send()
                .join();
        ProcessInstanceAssert assertions = BpmnAssert.assertThat(event);
    }

    @Test
    public void testProcessByInstanceKey() {
        Optional<InspectedProcessInstance> firstProcessInstance =
                InspectionUtility.findProcessInstances()
                        .withParentProcessInstanceKey(2251799814458246L)
                        .withBpmnProcessId("Process_Stock")
                        .findFirstProcessInstance();
        ProcessInstanceAssert assertions = BpmnAssert.assertThat(firstProcessInstance.get());
    }
    */


}
