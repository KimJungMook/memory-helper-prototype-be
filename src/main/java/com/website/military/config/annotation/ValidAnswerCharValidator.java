package com.website.military.config.annotation;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAnswerCharValidator implements ConstraintValidator<ValidAnswerChar, Character>{

    private static final Set<Character> VALID_ANSWERS = Set.of('A', 'B', 'C', 'D');

    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        if (value == null) return false;  // null 허용 여부는 상황에 따라 조정
        return VALID_ANSWERS.contains(Character.toUpperCase(value));
    }
}
