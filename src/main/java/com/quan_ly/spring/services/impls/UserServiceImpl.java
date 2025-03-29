package com.quan_ly.spring.services.impls;

import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.UserRepository;
import com.quan_ly.spring.services.UserService;
import com.quan_ly.spring.utils.EncrytedPasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
