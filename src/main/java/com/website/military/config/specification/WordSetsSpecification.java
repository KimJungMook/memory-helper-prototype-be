package com.website.military.config.specification;

import org.springframework.data.jpa.domain.Specification;

import com.website.military.domain.Entity.WordSets;

public class WordSetsSpecification {
    
    public static Specification<WordSets> userIdEquals(Long userId) {
        return (root, query, cb) ->
                userId != null ? cb.equal(root.get("user").get("userId"), userId) : null;
    }

    public static Specification<WordSets> nameContains(String name) {
        return (root, query, cb) ->
                (name != null && !name.isEmpty())
                        ? cb.like(root.get("setName"), "%" + name + "%")
                        : null;
    }
}
