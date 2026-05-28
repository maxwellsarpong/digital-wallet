package com.example.DigitalWallet.mapper;

import com.example.DigitalWallet.dto.response.user.UserResponse;
import com.example.DigitalWallet.entity.User;

public class MapUserResponse {
    public static UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
