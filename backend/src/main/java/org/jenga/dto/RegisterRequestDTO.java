package org.jenga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String username;

    private String displayName;

    @NotBlank
    private String password;
}
