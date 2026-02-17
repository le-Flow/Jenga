package org.jenga.tool;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import io.quarkus.logging.Log;

import org.jenga.dto.UserDTO;
import org.jenga.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class GetAllUsersTool {

    private final UserService userService;

    @Tool("Get a list of all user usernames in the system.")
    public String getAllUsers() {
        Log.debug("GetAllUsersTool.getAllUsers called");
        try {
            List<UserDTO> users = userService.findAll();

            if (users == null || users.isEmpty()) {
                return "No users are available in the system.";
            }

            return "Available Users:\n" +
                    users.stream()
                            .map(UserDTO::getUsername)
                            .sorted()
                            .collect(Collectors.joining(", "));

        } catch (Exception e) {
            Log.errorf(e, "GetAllUsersTool: Unexpected error: %s", e.getMessage());
            return "ERROR: An unexpected error occurred while fetching users: " + e.getMessage();
        }
    }
}