package com.example.login.service;


import com.example.login.model.User;
import com.example.login.model.UserDTO;

import java.util.List;

public interface UserService {
    List<User> findAll();
    void deleteUser(String email);
    User registerUser(UserDTO userDTO);
}
