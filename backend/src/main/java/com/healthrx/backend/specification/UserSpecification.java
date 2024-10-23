package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> roleEquals(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }
}
