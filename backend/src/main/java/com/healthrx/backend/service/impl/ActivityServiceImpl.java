package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.ActivitiesResponse;
import com.healthrx.backend.api.external.activities.UserActivityResponse;
import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.ActivityLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.mapper.ActivityMapper;
import com.healthrx.backend.repository.ActivityLogRepository;
import com.healthrx.backend.repository.ActivityRepository;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.specification.ActivityLogSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public PageResponse<UserActivityResponse> getUserActivities(Integer page, Integer size, String sortBy, String order, String activityId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = principalSupplier.get();

        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<ActivityLog> spec = Specification.where(ActivityLogSpecification.userIdEquals(user.getId()));
        if (activityId != null) spec = spec.and(ActivityLogSpecification.activityIdEquals(activityId));
        if (startDate != null) spec = spec.and(ActivityLogSpecification.activityTimeBetweenStartDate(startDate));
        if (endDate != null) spec = spec.and(ActivityLogSpecification.activityLogTimeBetweenEndDate(endDate));

        Page<ActivityLog> userActivitiesLogs = activityLogRepository.findAll(spec, pageable);

        List<UserActivityResponse> userActivityResponse = userActivitiesLogs.stream()
                .map(activityMapper::map)
                .toList();

        return new PageResponse<UserActivityResponse>()
                .setContent(userActivityResponse)
                .setCurrentPage(userActivitiesLogs.getNumber())
                .setPageSize(userActivitiesLogs.getSize())
                .setTotalElements(userActivitiesLogs.getTotalElements())
                .setLast(userActivitiesLogs.isLast())
                .setFirst(userActivitiesLogs.isFirst())
                .setTotalPages(userActivitiesLogs.getTotalPages());
    }

    private List<Activity> getActivities() {
        return activityRepository.findAll();
    }
}
