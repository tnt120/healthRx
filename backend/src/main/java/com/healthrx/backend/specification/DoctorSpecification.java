package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

@Slf4j
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

            List<String> specializationIds = Arrays.asList(specializationId.split(","));

            return specializationJoin.get("id").in(specializationIds);
        });
    }

    public static Specification<User> isVerifiedDoctor() {
        return (root, query, cb) -> cb.isTrue(root.get("isVerifiedDoctor"));
    }

    public static Specification<User> isNotInFriendsList(String userId) {
        return ((root, query, cb) -> {
            assert query != null;
            Subquery<String> subquery = query.subquery(String.class);
            Root<Friendship> friendshipRoot = subquery.from(Friendship.class);

            subquery.select(friendshipRoot.get("doctor").get("id"))
                    .where(cb.equal(friendshipRoot.get("user").get("id"), userId));

            return cb.not(root.get("id").in(subquery));
        });
    }
}
