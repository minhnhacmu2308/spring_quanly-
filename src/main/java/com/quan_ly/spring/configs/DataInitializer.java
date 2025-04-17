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
//    @Bean
//    CommandLineRunner initDatabase(UserRepository userRepository) {
//        return args -> {
//            String managerEmail = "manager@gmail.com";
//
//            if (userRepository.findByEmail(managerEmail).isEmpty()) {
//                User manager = new User();
//                manager.setFullName("Default Manager");
//                manager.setEmail(managerEmail);
//
//                // Mã hóa mật khẩu
//                manager.setPasswordHash(encrytedPasswordUtils.md5("123456"));
//
//                manager.setRole(Role.MANAGER);
//                userRepository.save(manager);
//
//                System.out.println("✅ Created default MANAGER user.");
//            } else {
//                System.out.println("ℹ️ MANAGER user already exists.");
//            }
//        };
//    }
@Bean
CommandLineRunner initDatabase(UserRepository userRepository) {
    return args -> {
        createUserIfNotExists(userRepository, "manager@gmail.com", "Manager", "123456", Role.MANAGER);
        createUserIfNotExists(userRepository, "fieldstaff@gmail.com", "Field Staff", "123456", Role.FIELD_STAFF);
        createUserIfNotExists(userRepository, "planner@gmail.com", "Planner", "123456", Role.PLANNER);
        createUserIfNotExists(userRepository, "accountant@gmail.com", "Accountant", "123456", Role.ACCOUNTANT);
    };
}

    private void createUserIfNotExists(UserRepository userRepository, String email, String fullName, String rawPassword, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(encrytedPasswordUtils.md5(rawPassword)); // Coi chừng, dùng MD5 thì nhớ nâng cấp bảo mật sau nha
            user.setRole(role);

            userRepository.save(user);
            System.out.println("✅ Created default " + role + " user with email: " + email);
        } else {
            System.out.println("ℹ️ User with email " + email + " already exists.");
        }
    }
}