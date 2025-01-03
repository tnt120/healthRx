package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.*;
import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.ActivityLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.mapper.ActivityMapper;
import com.healthrx.backend.repository.ActivityLogRepository;
import com.healthrx.backend.repository.ActivityRepository;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.specification.ActivityLogSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    private final AdminService adminService;
    private final ActivityRepository activityRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityMapper activityMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public List<ActivityDTO> getAllActivitiesDTOs() {
        adminService.checkPermissions();

        return getActivities().stream()
                .map(activityMapper::map)
                .toList();
    }

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
    public ActivityDTO addActivity(ActivityRequest req) {
        adminService.checkPermissions();

        activityRepository.findByName(req.getName()).ifPresent(activity -> {
            throw ACTIVITY_ALREADY_EXISTS.getError();
        });

        return activityMapper.map(activityRepository.save(Activity.builder()
                .name(req.getName())
                .metFactor(req.getMetFactor())
                .isPopular(req.getIsPopular())
                .build()));
    }

    @Override
    public ActivityDTO editActivity(String id, ActivityRequest req) {
        adminService.checkPermissions();

        Activity activity = activityRepository.findById(id)
                .orElseThrow(ACTIVITY_NOT_FOUND::getError);

        Optional.ofNullable(req.getName()).ifPresent(name -> {
            activityRepository.findByName(name).ifPresent(a -> {
                throw ACTIVITY_ALREADY_EXISTS.getError();
            });
            activity.setName(name);
        });

        Optional.ofNullable(req.getMetFactor()).ifPresent(activity::setMetFactor);
        Optional.ofNullable(req.getIsPopular()).ifPresent(activity::setIsPopular);

        return activityMapper.map(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public Void deleteActivity(String id) {
        adminService.checkPermissions();

        Activity activity = activityRepository.findById(id)
                .orElseThrow(ACTIVITY_NOT_FOUND::getError);

        activityLogRepository.deleteAllByActivityId(id);
        activityRepository.delete(activity);
        return null;
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

    @Override
    public UserActivityResponse addUserActivity(UserActivityRequest request) {
        User user = principalSupplier.get();

        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(ACTIVITY_NOT_FOUND::getError);

        ActivityLog activityLog = activityLogRepository.save(ActivityLog.builder()
                        .activity(activity)
                        .user(user)
                        .activityTime(request.getActivityTime())
                        .duration(request.getDuration())
                        .averageHeartRate(request.getAverageHeartRate())
                        .caloriesBurned(request.getCaloriesBurned())
                        .build());

        return activityMapper.map(activityLog);
    }

    @Override
    public UserActivityResponse editUserActivity(UserActivityRequest request, String id) {
        User user = principalSupplier.get();

        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(ACTIVITY_LOG_NOT_FOUND::getError);

        if (!activityLog.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        activityLog.setActivityTime(request.getActivityTime());
        activityLog.setDuration(request.getDuration());
        activityLog.setAverageHeartRate(request.getAverageHeartRate());
        activityLog.setCaloriesBurned(request.getCaloriesBurned());

        return activityMapper.map(activityLogRepository.save(activityLog));
    }

    @Override
    public Void deleteUserActivity(String id) {
        User user = principalSupplier.get();

        ActivityLog activityLog = activityLogRepository.findById(id)
                .orElseThrow(ACTIVITY_LOG_NOT_FOUND::getError);

        if (!activityLog.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        activityLogRepository.delete(activityLog);
        return null;
    }

    private List<Activity> getActivities() {
        return activityRepository.findAll();
    }
}
