package org.geektimes.projects.user.validator.bean.validation.annotation;

import org.geektimes.projects.user.validator.bean.validation.UserValidAnnotationValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UserValidAnnotationValidator.class})
public @interface UserValid {
}
