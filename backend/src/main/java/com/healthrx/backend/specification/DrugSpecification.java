package com.healthrx.backend.specification;

import com.healthrx.backend.api.internal.Drug;
import org.springframework.data.jpa.domain.Specification;

public class DrugSpecification {
    public static Specification<Drug> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
