package org.jenga.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String username;
    private String token;
    private String expirationDate;
}
