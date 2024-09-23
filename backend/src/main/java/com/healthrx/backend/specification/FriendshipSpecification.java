package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.domain.Specification;

public class FriendshipSpecification {
    public static Specification<Friendship> userFirstNameContains(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("user").get("firstName"), "%" + firstName + "%");
    }

    public static Specification<Friendship> userLastNameContains(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("user").get("lastName"), "%" + lastName + "%");
    }

    public static Specification<Friendship> doctorFirstNameContains(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("doctor").get("firstName"), "%" + firstName + "%");
    }

    public static Specification<Friendship> doctorLastNameContains(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("doctor").get("lastName"), "%" + lastName + "%");
    }

    public static Specification<Friendship> isAccepted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), "ACCEPTED");
    }
}
