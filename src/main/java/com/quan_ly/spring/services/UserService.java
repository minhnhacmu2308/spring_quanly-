package com.quan_ly.spring.services;

import com.quan_ly.spring.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> loginUser(String email, String password);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    boolean emailExists(String email);
}