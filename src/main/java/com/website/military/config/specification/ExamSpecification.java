package com.website.military.config.specification;

import org.springframework.data.jpa.domain.Specification;

import com.website.military.domain.Entity.Exam;

public class ExamSpecification {

    public static Specification<Exam> userIdEquals(Long userId) {
        return (root, query, cb) ->
                userId != null ? cb.equal(root.get("user").get("userId"), userId) : null;
    }

    public static Specification<Exam> nameContains(String name) {
        return (root, query, cb) ->
                (name != null && !name.isEmpty())
                        ? cb.like(root.get("examName"), "%" + name + "%")
                        : null;
    }

    public static Specification<Exam> setIdEquals(Long setId) {
        return (root, query, cb) ->
                setId != null ? cb.equal(root.get("wordsets").get("setId"), setId) : null;
    }
}
