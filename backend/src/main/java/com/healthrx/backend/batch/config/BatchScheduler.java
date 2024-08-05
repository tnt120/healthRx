package com.healthrx.backend.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job drugJob;

    @Scheduled(cron = "0 0 2 * * ?")
    public void perform() throws Exception {
        log.info("Fetching drugs job started at :{}", System.currentTimeMillis());
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addLong("time", System.currentTimeMillis());
        jobLauncher.run(drugJob, jobParametersBuilder.toJobParameters());
    }
}
