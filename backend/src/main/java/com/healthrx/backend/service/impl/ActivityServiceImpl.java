package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.activities.ActivitiesResponse;
import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.mapper.ActivityMapper;
import com.healthrx.backend.repository.ActivityLogRepository;
import com.healthrx.backend.repository.ActivityRepository;
import com.healthrx.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityMapper activityMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public ActivitiesResponse getAllActivities() {
        User user = principalSupplier.get();

        List<Activity> logs = activityLogRepository.findLastLoggedActivities(user.getId());

        Set<Activity> logSet = new HashSet<>(logs);

        List<Activity> activities = getActivities();

        List<Activity> popularActivities = new ArrayList<>(logs);
        List<Activity> otherActivities = new ArrayList<>();

        for (Activity activity : activities) {
            if (!logSet.contains(activity)) {
                if (activity.getIsPopular()) {
                    popularActivities.add(activity);
                } else {
                    otherActivities.add(activity);
                }
            }
        }

        return ActivitiesResponse.builder()
                .mostPopularActivities(popularActivities.stream().map(activityMapper::map).toList())
                .otherActivities(otherActivities.stream().map(activityMapper::map).toList())
                .build();
    }

    private List<Activity> getActivities() {
        return activityRepository.findAll();
    }
}
