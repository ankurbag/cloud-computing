package com.csye6225.spring2018.validator;

import com.csye6225.spring2018.model.User;
import com.csye6225.spring2018.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        User u = (User)obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","NotEmpty","Email address can't be empty!");
       // errors.rejectValue("email","Empty email ");
        System.out.print("one pass");
        if(userService.findUserByEmail(u.getEmail())!=null){
            System.out.print("second ");
            errors.rejectValue("email","Duplicate.user.email","User Already Exist!!!");
        }
    }
}
