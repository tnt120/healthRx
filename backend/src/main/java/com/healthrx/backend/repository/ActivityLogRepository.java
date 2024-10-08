package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    @Query("SELECT al.activity FROM ActivityLog al JOIN al.activity a WHERE al.user.id = :userId ORDER BY al.activityTime DESC LIMIT 3")
    List<Activity> findLastLoggedActivities(
            @Param("userId") String userId
    );
}
