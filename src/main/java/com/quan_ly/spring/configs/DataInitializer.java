package com.quan_ly.spring.configs;

import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.UserRepository;
import com.quan_ly.spring.utils.EncrytedPasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    EncrytedPasswordUtils encrytedPasswordUtils;
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            String managerEmail = "manager@gmail.com";

            if (userRepository.findByEmail(managerEmail).isEmpty()) {
                User manager = new User();
                manager.setFullName("Default Manager");
                manager.setEmail(managerEmail);

                // Mã hóa mật khẩu
                manager.setPasswordHash(encrytedPasswordUtils.md5("123456"));

                manager.setRole(Role.MANAGER);
                userRepository.save(manager);

                System.out.println("✅ Created default MANAGER user.");
            } else {
                System.out.println("ℹ️ MANAGER user already exists.");
            }
        };
    }
}