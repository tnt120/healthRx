package com.healthrx.backend.quartz;

import com.healthrx.backend.api.internal.enums.Days;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationSchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    public void scheduleDrugNotification(String jobName, String email, QuartzNotificationDrugsModel drugsModel) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        LocalDate now = LocalDate.now();
        LocalDate startDate = drugsModel.getStartDate();
        if (drugsModel.getStartDate().isBefore(now)) {
            startDate = now;
        }

        for (Days day : drugsModel.getDays()) {
            List<LocalTime> mutableTimes = new ArrayList<>(drugsModel.getTimes());
            Collections.sort(mutableTimes);
            LocalTime time = mutableTimes.getLast().plusMinutes(15);

            String jobKeyString = getJobKeyString(jobName, day, time, drugsModel.getUserDrugId());

            JobDataMap jobDataMap = createBasicDataMap(email, "drugReminder");
            jobDataMap.put("drugName", drugsModel.getDrugName());
            jobDataMap.put("userDrugId", drugsModel.getUserDrugId());
            jobDataMap.put("times", drugsModel.getTimes()
                    .stream()
                    .map(LocalTime::toString)
                    .collect(Collectors.joining(",")));
            JobDetail jobDetail = createJobDetail(jobKeyString, "drugReminder", jobDataMap);

            String cronExpression = createCronExpression(day, time);

            Trigger trigger = createTrigger(jobKeyString, "drugReminder", cronExpression, startDate, drugsModel.getEndDate());

            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Triggering job for drugs:  day: {}, time: {}, email: {}, drug: {}, cron: {}", day, time, email, drugsModel.getDrugName(), cronExpression);
        }
    }

    public void scheduleParameterNotification(String jobName, String email, QuartzNotificationParametersModel parametersModel) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        LocalDate startDate = LocalDate.now();

        String jobKeyString = getJobKeyString(jobName, null, parametersModel.getTime(), parametersModel.getUserId());

        JobDataMap jobDataMap = createBasicDataMap(email, "parameterReminder");
        jobDataMap.put("userId", parametersModel.getUserId());

        JobDetail jobDetail = createJobDetail(jobKeyString, "parameterReminder", jobDataMap);

        String cronExpression = createCronExpression(null, parametersModel.getTime());

        Trigger trigger = createTrigger(jobKeyString, "parameterReminder", cronExpression, startDate, null);

        scheduler.scheduleJob(jobDetail, trigger);

        log.info("Triggering job for parameters: time: {}, email: {}, userId: {}, cron: {}", parametersModel.getTime(), email, parametersModel.getUserId(), cronExpression);
    }

    public void deleteDrugNotification(String jobName, String userDrugId, List<Days> days, List<LocalTime> times) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        List<LocalTime> mutableTimes = new ArrayList<>(times);
        Collections.sort(mutableTimes);
        LocalTime time = mutableTimes.getLast().plusMinutes(15);

        for (Days day: days) {
            String jobKeyString = getJobKeyString(jobName, day, time, userDrugId);
            JobKey jobKey = new JobKey(jobKeyString, "drugReminder");

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }

    public void deleteParameterNotification(String jobName, String userId, LocalTime time) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();

        String jobKeyString = getJobKeyString(jobName, null, time, userId);
        JobKey jobKey = new JobKey(jobKeyString, "parameterReminder");

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    private JobDataMap createBasicDataMap(String email, String group) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("group", group);
        jobDataMap.put("email", email);
        return jobDataMap;
    }

    private JobDetail createJobDetail(String jobKeyString, String group, JobDataMap jobDataMap) {
        return JobBuilder.newJob(ReminderJob.class)
                .withIdentity(jobKeyString, group)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger createTrigger(String jobKeyString, String group, String cronExpression, LocalDate startAt, LocalDate endAt) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKeyString + "trigger", group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .startAt(java.sql.Date.valueOf(startAt))
                .endAt(endAt != null ? java.sql.Date.valueOf(endAt) : null)
                .build();
    }

    private String getJobKeyString(String jobName, Days day, LocalTime time, String id) {
        if (day == null) {
            return jobName + "-" + id + "-" + time;
        }
        return jobName + "-" + id + "-" + day + "-" + time;
    }

    private String createCronExpression(Days day, LocalTime time) {
        if (day != null) {
            int dayOfWeek = day.ordinal() + 1;
            return String.format("0 %d %d ? * %d", time.getMinute(), time.getHour(), dayOfWeek);
        }

        return String.format("0 %d %d ? * *", time.getMinute(), time.getHour());
    }

}
