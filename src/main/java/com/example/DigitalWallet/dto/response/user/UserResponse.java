package com.example.DigitalWallet.dto.response.user;

import com.example.DigitalWallet.entity.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;

    private Boolean verified;

    private Boolean active;

    private LocalDateTime createdAt;
}
