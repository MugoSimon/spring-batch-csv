package com.thimionii.spring_batch_csv.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerBatchController {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Autowired
    public CustomerBatchController(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @PostMapping("/startBatch")
    public String startBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return "Batch job has been invoked.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start batch job: " + e.getMessage();
        }
    }
}