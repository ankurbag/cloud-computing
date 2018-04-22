package com.csye6225.spring2018.service;

import com.csye6225.spring2018.model.User;
import com.csye6225.spring2018.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;



    public User findByEmail(String userName) {
        return userRepository.findByEmail(userName);
    }

    public void saveUser(User user){
        System.out.print(user.getPassword());

       // user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        // user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User user1= userRepository.findUserByEmail(email);
        return user1;
    }


}
/*public interface UserService {
    public void saveUser(User user);
    public User findUserByEmail(String email);
    public User findByEmail(String email);

}*/
