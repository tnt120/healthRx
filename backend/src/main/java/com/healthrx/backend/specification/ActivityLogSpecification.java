package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.ActivityLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ActivityLogSpecification {
    public static Specification<ActivityLog> userIdEquals(String userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ActivityLog> activityIdEquals(String activityId) {
        return (root, query, cb) -> cb.equal(root.get("activity").get("id"), activityId);
    }

    public static Specification<ActivityLog> activityTimeBetweenStartDate(LocalDateTime startDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("activityTime"), startDate);
    }

    public static Specification<ActivityLog> activityLogTimeBetweenEndDate(LocalDateTime endDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("activityTime"), endDate);
    }
}
