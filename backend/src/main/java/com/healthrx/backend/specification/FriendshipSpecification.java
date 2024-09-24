package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.chat.Friendship;
import org.springframework.data.jpa.domain.Specification;

public class FriendshipSpecification {
    public static Specification<Friendship> userFirstNameContains(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("user").get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Friendship> userLastNameContains(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("user").get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Friendship> doctorFirstNameContains(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("doctor").get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Friendship> doctorLastNameContains(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("doctor").get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Friendship> isAccepted() {
        return (root, query, cb) -> cb.equal(root.get("status"), "ACCEPTED");
    }
}
