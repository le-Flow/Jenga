package org.jenga.service;

import org.jenga.dto.LoginRequestDTO;
import org.jenga.dto.LoginResponseDTO;
import org.jenga.dto.RegisterRequestDTO;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.quarkus.test.TestTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jakarta.inject.Inject;

import jakarta.ws.rs.BadRequestException;

@QuarkusTest
class AuthenticationServiceTest{
    @Inject
    AuthenticationService service;

    private static final String EMAIL = "user@test.com";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    @Test
    @TestTransaction
    void testValidRegistration() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        LoginResponseDTO response = service.register(dto);

        assertEquals(response.getUsername(), USERNAME);
        assertEquals(response.getDisplayName(), USERNAME);

        assertNotNull(response.getToken());
        assertFalse(response.getToken().isBlank());

        assertNotNull(response.getExpiresIn());
        assertTrue(response.getExpiresIn() > 0);
    }

    @Test
    @TestTransaction
    void testValidRegistrationWithDisplayName() { 
        String displayName = "Test User";

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        dto.setDisplayName(displayName);

        LoginResponseDTO response = service.register(dto);

        assertEquals(response.getDisplayName(), displayName);
    }

    @Test
    @TestTransaction
    void testUserAlreadyExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        service.register(dto);

        assertThrows(BadRequestException.class, () -> service.register(dto));
    }

    @Test
    @TestTransaction
    void testLogin() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);

        service.register(dto);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        
        // Invalid username
        loginDTO.setUsername("false7h8g2z8gb82bg");
        loginDTO.setPassword(PASSWORD);

        assertThrows(BadRequestException.class, () -> service.login(loginDTO));

        // Invalid password 
        loginDTO.setUsername(USERNAME);
        loginDTO.setPassword("falsegbobzgz8obg");

        assertThrows(BadRequestException.class, () -> service.login(loginDTO));
        
        // Empty password
        loginDTO.setUsername(USERNAME);
        loginDTO.setPassword("");

        assertThrows(BadRequestException.class, () -> service.login(loginDTO));

        // Valid login
        loginDTO.setUsername(USERNAME);
        loginDTO.setPassword(PASSWORD);

        LoginResponseDTO response = service.login(loginDTO);

        assertEquals(response.getUsername(), USERNAME);
        assertEquals(response.getDisplayName(), USERNAME);

        assertNotNull(response.getToken());
        assertFalse(response.getToken().isBlank());

        assertNotNull(response.getExpiresIn());
        assertTrue(response.getExpiresIn() > 0);
    }
}
