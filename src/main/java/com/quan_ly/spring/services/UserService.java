package com.quan_ly.spring.services;

import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    public List<User> getUsersExcludingManager();
    public List<User> getUsersByRole(Role role);
    List<User> findByRoleAndNotInActiveProjects(@Param("role") Role role, @Param("now") LocalDate now);


}