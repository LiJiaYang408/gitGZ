package com.example.rearend.service;

import com.example.rearend.mapper.UserMapper;
import com.example.rearend.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public boolean validateUser(String username, String password) {
        User user = findByUsername(username);
        return user !=null && user.getPassword().equals(password);
    }
}