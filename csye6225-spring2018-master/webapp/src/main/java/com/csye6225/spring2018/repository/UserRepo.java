package com.csye6225.spring2018.repository;

import com.csye6225.spring2018.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepo extends JpaRepository<User,Integer> {
    User findUserByEmail(String email);
    User findByEmail(String email);
}
