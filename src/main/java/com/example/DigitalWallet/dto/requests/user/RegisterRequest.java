package com.example.DigitalWallet.dto.requests.user;

import com.example.DigitalWallet.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "LastName is required")
    private String lastName;

    @NotBlank(message = "FirstName is required")
    private String firstName;

    @Email
    private String email;

    @NotBlank(message = "Phone is required")
    private String phoneNumber;

    private String password;

    private LocalDate dateOfBirth;

    private String nationalId;
    private Role role;
}
