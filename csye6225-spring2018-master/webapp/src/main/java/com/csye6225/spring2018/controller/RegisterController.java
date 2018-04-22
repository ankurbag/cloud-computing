package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.model.User;
import com.csye6225.spring2018.service.UserService;
import com.csye6225.spring2018.validator.UserValidator;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class RegisterController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value="/register",method= RequestMethod.GET)
    public String registerUser(Model model){
    System.out.println("Say Register");
    model.addAttribute("user",new User());
    return "register";

    }

    @RequestMapping(value="/register",method=RequestMethod.POST)
    public String doRegistration(@ModelAttribute @Valid User user, BindingResult bindingResult){
        userValidator.validate(user,bindingResult);

        for (FieldError err:bindingResult.getFieldErrors()){
            System.out.println(user.getEmail());
            System.out.println(err.getDefaultMessage()); // Output: must be greater than or equal to 1
        }
        if(bindingResult.hasErrors()) {
            System.out.print("from validator");
            return "register";

        }

        boolean validEmail = validateEmail(user.getEmail());
        JsonObject jsonObject = new JsonObject();

        if(!validEmail) {
            System.out.print("from email");
           return "register";
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            System.out.println(user.getFirstName() + " " + user.getEmail());

        return "registerSuccess";
    }



    public boolean validateEmail(String email){

        Pattern email_pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher match = email_pattern.matcher(email);
        return match.matches();
    }
}
