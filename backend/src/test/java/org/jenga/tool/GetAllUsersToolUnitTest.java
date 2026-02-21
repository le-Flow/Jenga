package org.jenga.tool;

import org.jenga.dto.UserDTO;
import org.jenga.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GetAllUsersToolUnitTest {

    @Mock
    UserService userService;

    @InjectMocks
    GetAllUsersTool getAllUsersTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers_WithUsers() {
        UserDTO u1 = new UserDTO();
        u1.setUsername("alice");

        UserDTO u2 = new UserDTO();
        u2.setUsername("bob");

        when(userService.findAll()).thenReturn(Arrays.asList(u2, u1));

        String result = getAllUsersTool.getAllUsers();

        assertTrue(result.contains("Available Users:"));
        // The tool sorts them
        assertTrue(result.contains("alice, bob"));
    }

    @Test
    void testGetAllUsers_Empty() {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        String result = getAllUsersTool.getAllUsers();

        assertEquals("No users are available in the system.", result);
    }

    @Test
    void testGetAllUsers_Error() {
        when(userService.findAll()).thenThrow(new RuntimeException("Service failure"));

        String result = getAllUsersTool.getAllUsers();

        assertTrue(result.startsWith("ERROR: An unexpected error occurred"));
        assertTrue(result.contains("Service failure"));
    }
}
