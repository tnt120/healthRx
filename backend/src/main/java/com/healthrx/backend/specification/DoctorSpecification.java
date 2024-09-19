package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.User;
import org.springframework.data.jpa.domain.Specification;

public class DoctorSpecification {
    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> cityEquals(String cityId) {
        return ((root, query, cb) -> {
            var doctorDetailsJoin = root.join("doctorDetails");
            var cityJoin = doctorDetailsJoin.join("city");
            return cb.equal(cityJoin.get("id"), cityId);
        });
    }

    public static Specification<User> specificationEquals(String specializationId) {
        return ((root, query, cb) -> {
            var doctorDetailsJoin = root.join("doctorDetails");
            var specializationJoin = doctorDetailsJoin.join("doctorSpecializations").join("specialization");
            return cb.equal(specializationJoin.get("id"), specializationId);
        });
    }

    public static Specification<User> isVerifiedDoctor() {
        return (root, query, cb) -> cb.isTrue(root.get("isVerifiedDoctor"));
    }
}
