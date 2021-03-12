package org.geektimes.projects.user.validator.bean.validation;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.validator.bean.validation.annotation.UserValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @ClassName: UserValidator
 * @Description: 自定义校验器
 * @author: zhoujian
 * @date: 2021/3/12 20:50
 * @version: 1.0
 */
public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    @Override
    public void initialize(UserValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
