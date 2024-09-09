package com.healthrx.backend.quartz;

import com.healthrx.backend.api.internal.DrugLog;
import com.healthrx.backend.api.internal.UserDrug;
import com.healthrx.backend.kafka.KafkaReceiveModel;
import com.healthrx.backend.repository.DrugLogRepository;
import com.healthrx.backend.repository.UserDrugRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.healthrx.backend.handler.BusinessErrorCodes.DRUG_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderJob implements Job {
    private final KafkaTemplate<String, KafkaReceiveModel> kafkaTemplate;
    private final UserDrugRepository userDrugRepository;
    private final DrugLogRepository drugLogRepository;

    @Override
    @SneakyThrows
    public void execute(JobExecutionContext context) {
        String email = (String) context.getJobDetail().getJobDataMap().get("email");
        String group = (String) context.getJobDetail().getJobDataMap().get("group");

        switch (group) {
            case "drugReminder": {
                String drugName = (String) context.getJobDetail().getJobDataMap().get("drugName");
                String userDrugId = (String) context.getJobDetail().getJobDataMap().get("userDrugId");
                String timesAsString = (String) context.getJobDetail().getJobDataMap().get("times");

                List<LocalTime> times = Arrays.stream(timesAsString.split(","))
                        .map(LocalTime::parse)
                        .toList();

                handleDrugNotification(email, drugName, userDrugId, times);

                break;
            }
            case "parameterReminder": {
                break;
            }
            default: {
                log.info("Cannot recognize group from quartz job");
            }
        }


    }

    private void handleDrugNotification(String email, String drugName, String userDrugId, List<LocalTime> times) {
        UserDrug userDrug = userDrugRepository.findById(userDrugId)
                .orElseThrow(DRUG_NOT_FOUND::getError);

        List<LocalTime> missingTimes = new ArrayList<>();

        times.forEach(time -> {
            DrugLog drugLog = drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(
                    userDrug.getDrug().getId(),
                    userDrug.getUser().getId(),
                    time
            ).orElse(null);

            if (drugLog == null) {
                missingTimes.add(time);
            }
        });

        if (!missingTimes.isEmpty()) {
            String missingTimesString = missingTimes.stream()
                    .map(LocalTime::toString)
                    .collect(Collectors.joining(", "));

            Map<String, String> data = Map.of(
                    "link", "http://localhost:4200/user/cabinet",
                    "drugName", drugName,
                    "missingTimes", missingTimesString
            );

            sendMail(Collections.singletonList(email), data);
        }
    }

    private void sendMail(List<String> emails, Map<String, String> data) {
        KafkaReceiveModel kafkaReceiveModel = new KafkaReceiveModel()
                .setSubject("Drug reminder")
                .setStrategy("DRUG_REMINDER")
                .setEmails(emails)
                .setData(data);

        this.kafkaTemplate.send("mails", kafkaReceiveModel);
    }
}
