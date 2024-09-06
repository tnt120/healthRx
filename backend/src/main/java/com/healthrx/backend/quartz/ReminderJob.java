package com.healthrx.backend.quartz;

import com.healthrx.backend.kafka.KafkaReceiveModel;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReminderJob implements Job {
    private final KafkaTemplate<String, KafkaReceiveModel> kafkaTemplate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String email = (String) context.getJobDetail().getJobDataMap().get("email");
        String drugName = (String) context.getJobDetail().getJobDataMap().get("drugName");

        sendMail(List.of(email), drugName);
    }

    private void sendMail(List<String> emails, String drugName) {
        KafkaReceiveModel kafkaReceiveModel = new KafkaReceiveModel()
                .setSubject("Drug reminder")
                .setStrategy("DRUG_REMINDER")
                .setEmails(emails)
                .setData(Map.of(
                        "link", "http://localhost:4200/user/cabinet",
                        "drugName", drugName
                ));

        this.kafkaTemplate.send("mails", kafkaReceiveModel);
    }
}
