package com.ayushcart.dto;

import com.ayushcart.entity.Role;
import com.ayushcart.entity.User;

/** What the API exposes about a user — note: no password field. */
public record UserResponse(Long id, String fullName, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
