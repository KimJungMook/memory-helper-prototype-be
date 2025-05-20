package com.website.military.config.annotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Constraint(validatedBy = ValidAnswerCharValidator.class)
@Target({ TYPE_USE })  // 리스트 원소에 붙이려면 TYPE_USE 필수!
@Retention(RUNTIME)
public @interface ValidAnswerChar{
    String message() default "정답은 A, B, C, D 중 하나여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
