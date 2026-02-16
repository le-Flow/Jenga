package org.jenga.service;

import java.util.List;

import org.jenga.dto.RegisterRequestDTO;
import org.jenga.dto.UserDTO;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.quarkus.test.TestTransaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import jakarta.inject.Inject;

import jakarta.ws.rs.NotFoundException;

@QuarkusTest
class UserServiceTest {
    @Inject
    AuthenticationService authService;

    @Inject
    UserService service;

    private static final String EMAIL = "user@test.com";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    @Test
    @TestTransaction
    void testGettingUser() {
        assertThrows(NotFoundException.class, () -> service.findByUsername(USERNAME));

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(EMAIL);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        authService.register(dto);
        
        UserDTO user = service.findByUsername(USERNAME);
        assertNotNull(user);
        assertEquals(user.getUsername(), USERNAME);

        List<UserDTO> users = service.findAll();
        UserDTO foundUser = users.stream()
            .filter(u -> u.getUsername().equals(USERNAME))
            .findFirst()
            .orElseThrow(() -> new AssertionError());

        assertEquals(foundUser.getUsername(), USERNAME);
    }

    @Test
    @TestTransaction
    void testGettingNonexistingUser() {
        assertThrows(NotFoundException.class, () -> service.findByUsername("nonexisting1ufn1f"));
    }
}
