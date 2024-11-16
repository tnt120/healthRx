package com.healthrx.backend.services;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.external.activities.ActivityRequest;
import com.healthrx.backend.api.external.activities.UserActivityResponse;
import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.ActivityLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.mapper.ActivityMapper;
import com.healthrx.backend.repository.ActivityLogRepository;
import com.healthrx.backend.repository.ActivityRepository;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.impl.ActivityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ActivityServiceTest {

    @Mock
    private AdminService adminService;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private Supplier<User> principalSupplier;

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Test
    void shouldGetAllActivitiesDTOsWhenUserHasPermission() {
        List<Activity> activities = List.of(
                new Activity().setId("1").setName("Run").setMetFactor(8.0).setIsPopular(true),
                new Activity().setId("2").setName("Walk").setMetFactor(3.5).setIsPopular(false)
        );

        List<ActivityDTO> expectedDtos = List.of(
                new ActivityDTO("1", "Run", 8.0, true),
                new ActivityDTO("2", "Walk", 3.5, false)
        );

        Mockito.when(activityRepository.findAll()).thenReturn(activities);
        Mockito.when(activityMapper.map(Mockito.any(Activity.class))).thenAnswer(invocation -> {
            Activity activity = invocation.getArgument(0);
            return new ActivityDTO(activity.getId(), activity.getName(), activity.getMetFactor(), activity.getIsPopular());
        });

        List<ActivityDTO> result = activityService.getAllActivitiesDTOs();

        Mockito.verify(adminService).checkPermissions();
        Assertions.assertEquals(expectedDtos, result);
    }
    @Test
    void shouldAddActivityWhenNameIsUnique() {
        ActivityRequest request = new ActivityRequest("Run", 8.0, true);
        Activity savedActivity = new Activity().setId("1").setName("Run").setMetFactor(8.0).setIsPopular(true);
        ActivityDTO expectedDto = new ActivityDTO("1", "Run", 8.0, true);

        Mockito.when(activityRepository.findByName("Run")).thenReturn(Optional.empty());
        Mockito.when(activityRepository.save(Mockito.any(Activity.class))).thenReturn(savedActivity);
        Mockito.when(activityMapper.map(savedActivity)).thenReturn(expectedDto);

        ActivityDTO result = activityService.addActivity(request);

        Mockito.verify(adminService).checkPermissions();
        Mockito.verify(activityRepository).save(Mockito.any(Activity.class));
        Assertions.assertEquals(expectedDto, result);
    }

    @Test
    void shouldThrowErrorWhenActivityAlreadyExists() {
        ActivityRequest request = new ActivityRequest("Run", 8.0, true);
        Mockito.when(activityRepository.findByName("Run")).thenReturn(Optional.of(new Activity()));

        Assertions.assertThrows(RuntimeException.class, () -> activityService.addActivity(request));
        Mockito.verify(adminService).checkPermissions();
    }

    @Test
    void shouldEditActivityWhenValidIdIsProvided() {
        Activity existingActivity = new Activity().setId("1").setName("Run").setMetFactor(8.0).setIsPopular(true);
        ActivityRequest updateRequest = new ActivityRequest("Jogging", 7.5, false);
        Activity updatedActivity = new Activity().setId("1").setName("Jogging").setMetFactor(7.5).setIsPopular(false);
        ActivityDTO expectedDto = new ActivityDTO("1", "Jogging", 7.5, false);

        Mockito.when(activityRepository.findById("1")).thenReturn(Optional.of(existingActivity));
        Mockito.when(activityRepository.findByName("Jogging")).thenReturn(Optional.empty());
        Mockito.when(activityRepository.save(existingActivity)).thenReturn(updatedActivity);
        Mockito.when(activityMapper.map(updatedActivity)).thenReturn(expectedDto);

        ActivityDTO result = activityService.editActivity("1", updateRequest);

        Mockito.verify(adminService).checkPermissions();
        Assertions.assertEquals(expectedDto, result);
    }

    @Test
    void shouldDeleteActivityAndLogs() {
        Activity existingActivity = new Activity().setId("1").setName("Run");
        Mockito.when(activityRepository.findById("1")).thenReturn(Optional.of(existingActivity));

        activityService.deleteActivity("1");

        Mockito.verify(adminService).checkPermissions();
        Mockito.verify(activityLogRepository).deleteAllByActivityId("1");
        Mockito.verify(activityRepository).delete(existingActivity);
    }

    @Test
    void shouldReturnUserActivitiesWithPagination() {
        User user = new User().setId("1");
        Activity activity = new Activity()
                .setId("1")
                .setName("Run")
                .setMetFactor(8.0)
                .setIsPopular(true);
        ActivityLog activityLog = new ActivityLog()
                .setId("1")
                .setActivity(activity)
                .setUser(user)
                .setActivityTime(LocalDateTime.of(2024, 10, 1, 10, 0))
                .setDuration(30)
                .setAverageHeartRate(120)
                .setCaloriesBurned(200);

        LocalDateTime startDate = LocalDateTime.of(2024, 9, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 11, 1, 0, 0);

        Page<ActivityLog> logsPage = new PageImpl<>(List.of(activityLog));
        Mockito.when(principalSupplier.get()).thenReturn(user);
        Mockito.when(activityLogRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(logsPage);

        UserActivityResponse expectedResponse = UserActivityResponse.builder()
                .id("1")
                .activity(ActivityDTO.builder()
                        .id("1")
                        .name("Run")
                        .metFactor(8.0)
                        .isPopular(true)
                        .build())
                .activityTime(LocalDateTime.of(2024, 10, 1, 10, 0))
                .duration(30)
                .averageHeartRate(120)
                .caloriesBurned(200)
                .build();
        Mockito.when(activityMapper.map(activityLog)).thenReturn(expectedResponse);

        PageResponse<UserActivityResponse> result = activityService.getUserActivities(
                0, 10, "id", "asc", null, startDate, endDate);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getContent());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertTrue(result.isFirst());
        Assertions.assertTrue(result.isLast());
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(expectedResponse, result.getContent().getFirst());
    }


}
