package com.quan_ly.spring.services;

import com.quan_ly.spring.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> loginUser(String email, String password);
}
