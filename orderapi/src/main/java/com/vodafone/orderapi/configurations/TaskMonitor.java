/*
package com.vodafone.orderapi.configurations;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskMonitor {
    @Autowired
    private ZeebeClient zeebeClient;

    @Scheduled(fixedRate = 300000) // Check every 5 minutes
    public void autoCompleteTasks() {
        zeebeClient.newActivateJobsCommand()
                .jobType("Job Worker")
                .maxJobsToActivate(10)
                .send()
                .join()
                .getJobs()
                .forEach(job -> {
                    try {

                        zeebeClient.newCompleteCommand(job.getKey()).send().join();
                        System.out.println("Auto-completed task: " + job.getKey());
                    } catch (Exception e) {
                        System.err.println("Error completing task: " + e.getMessage());
                    }
                });
    }
}
*/
