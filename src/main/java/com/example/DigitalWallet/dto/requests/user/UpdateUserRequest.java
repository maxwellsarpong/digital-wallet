package com.example.DigitalWallet.dto.requests.user;

import com.example.DigitalWallet.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest{

    private String lastName;

    private String firstName;

    @Email
    private String email;

    private String phoneNumber;

    private String password;

    private LocalDate dateOfBirth;

    private String nationalId;
    private Role role;
}
