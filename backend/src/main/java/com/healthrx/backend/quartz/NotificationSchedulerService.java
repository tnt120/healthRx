package com.healthrx.backend.quartz;

import com.healthrx.backend.api.internal.enums.Days;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class NotificationSchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    public void scheduleNotification(String jobName, String userDrugId, List<Days> days, List<LocalTime> times, LocalDateTime startDate, LocalDateTime endDate, String email, String drugName) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        LocalDateTime now = LocalDateTime.now();
        if (startDate.isBefore(now)) {
            startDate = now.plusSeconds(5);
        }

        for (Days day : days) {
            for (LocalTime time : times) {
                String jobKeyString = getJobKeyString(jobName, day, time, userDrugId);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("email", email);
                jobDataMap.put("drugName", drugName);
                jobDataMap.put("userDrugId", userDrugId);

                JobDetail jobDetail = JobBuilder.newJob(ReminderJob.class)
                        .withIdentity(jobKeyString, "drugReminder")
                        .usingJobData(jobDataMap)
                        .build();

                String cronExpression = createCronExpression(day, time);

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobKeyString + "trigger", "drugReminder")
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .startAt(java.sql.Date.valueOf(startDate.toLocalDate()))
                        .endAt(endDate != null ? java.sql.Date.valueOf(endDate.toLocalDate()) : null)
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);

                log.info("Triggering email for day: {}, time: {}, email: {}, drug: {}, cron: {}", day, time, email, drugName, cronExpression);
            }
        }
    }

    public void deleteNotification(String jobName, String userDrugId, List<Days> days, List<LocalTime> times) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        for (Days day: days) {
            for (LocalTime time: times) {
                String jobKeyString = getJobKeyString(jobName, day, time, userDrugId);
                JobKey jobKey = new JobKey(jobKeyString, "drugReminder");

                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
            }
        }
    }

    private String getJobKeyString(String jobName, Days day, LocalTime time, String userDrugId) {
        return jobName + "-" + userDrugId + "-" + day + "-" + time;
    }

    private String createCronExpression(Days day, LocalTime time) {
        int dayOfWeek = day.ordinal() + 1;
        return String.format("0 %d %d ? * %d", time.getMinute(), time.getHour(), dayOfWeek);

    }

}
