package com.example.DigitalWallet.dto.response.user;

import com.example.DigitalWallet.entity.Account;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
