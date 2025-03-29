package com.quan_ly.spring.dtos;

import com.quan_ly.spring.enums.Role;

public class UserDTO {
    private String fullName;
    private String email;
    private Role role;

    public UserDTO(String fullName, String email, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}