package org.jenga.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String username;
    private String displayName;
    private String token;
    private int expiresIn;
}
