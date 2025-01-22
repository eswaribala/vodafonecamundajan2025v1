package com.vodafone.orderapi;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.DeploymentAssert;
import io.camunda.zeebe.process.test.assertions.MessageAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import io.camunda.zeebe.process.test.filters.RecordStream;
import io.camunda.zeebe.process.test.inspections.InspectionUtility;
import io.camunda.zeebe.process.test.inspections.model.InspectedProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

@ZeebeProcessTest
public class LoanProcessTest {
	
	private ZeebeClient zeebeClient;
    private ZeebeTestEngine zeebeTestEngine;
    private RecordStream recordStream;
    
    @BeforeEach
    public void deployProcessTest() {
    	
    DeploymentEvent event=zeebeClient.newDeployResourceCommand()
    	  .addResourceFromClasspath("processes/loanprocess.bpmn")
    	  .send()
    	  .join();
    	
    	DeploymentAssert assertions = BpmnAssert.assertThat(event);
    }
    
    @Test
    public void testProcessInstance() {
    ProcessInstanceEvent event=zeebeClient.newCreateInstanceCommand()
    	       .bpmnProcessId("Process_Loan")
    	       .latestVersion()
    	       .send()
    	       .join();
    
    ProcessInstanceAssert assertions=BpmnAssert.assertThat(event);
    	       
    }

    @Test
    public void testProcessInstanceByGeneratedKey() {
        Optional<InspectedProcessInstance> firstProcessInstance =
                InspectionUtility.findProcessInstances()
                        .withParentProcessInstanceKey(2251799813685944L)
                        .withBpmnProcessId("Process_Travel")
                        .findFirstProcessInstance();
        if(firstProcessInstance.isPresent()) {
            ProcessInstanceAssert assertions = BpmnAssert.assertThat(firstProcessInstance.get());
        }
    }

     @Test
     public void testActivatedJob(){
         testProcessInstanceByGeneratedKey();
         ActivateJobsResponse response = zeebeClient.newActivateJobsCommand()
                 .jobType("getRandomNo")
                 .maxJobsToActivate(1)
                 .send()
                 .join();
         if(!response.getJobs().isEmpty()) {
             List<ActivatedJob> activatedJob = response.getJobs();
             BpmnAssert.assertThat(activatedJob.get(0));
         }
     }

     @Test
     public void testMessageAssertions(){
         testProcessInstanceByGeneratedKey();
         PublishMessageResponse response = zeebeClient
                 .newPublishMessageCommand()
                 .messageName("Message_EMI_Ref")
                 .correlationKey("1001")
                 .send()
                 .join();
         MessageAssert assertions = BpmnAssert.assertThat(response);
     }

	
}
