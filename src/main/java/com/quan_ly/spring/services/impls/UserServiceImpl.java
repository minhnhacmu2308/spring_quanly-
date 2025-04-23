package com.quan_ly.spring.services.impls;

import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.UserRepository;
import com.quan_ly.spring.services.UserService;
import com.quan_ly.spring.utils.EncrytedPasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    EncrytedPasswordUtils encrytedPasswordUtils;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> loginUser(String email, String password) {
        String passwordMD5 = encrytedPasswordUtils.md5(password);
        return userRepository.findByEmail(email)
                .filter(user -> user.getPasswordHash().equals(passwordMD5));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        user.setPasswordHash(encrytedPasswordUtils.md5(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFullName(updatedUser.getFullName());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());

            if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
                user.setPasswordHash(encrytedPasswordUtils.md5(updatedUser.getPasswordHash()));
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent(); // Giả sử bạn dùng Optional
    }

    @Override
    public List<User> getUsersExcludingManager() {
        return userRepository.findByRoleNot(Role.MANAGER);
    }

    @Override
    public List<User> getUsersByRole(Role role){
        return userRepository.findByRole(role);
    };

    @Override
    public List<User> findByRoleAndNotInActiveProjects(@Param("role") Role role, @Param("now") LocalDate now){
        return userRepository.findByRoleAndNotInActiveProjects(role,now);
    };

}