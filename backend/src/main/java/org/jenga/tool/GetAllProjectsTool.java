package org.jenga.tool;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import io.quarkus.logging.Log;

import org.jenga.dto.ProjectResponseDTO;
import org.jenga.service.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class GetAllProjectsTool {

    private final ProjectService projectService;

    @Tool("Get a list of all available project IDs and their names.")
    public String getAllProjects() {
        Log.debug("GetAllProjectsTool.getAllProjects called");
        try {
            List<ProjectResponseDTO> projects = projectService.findAll();

            if (projects == null || projects.isEmpty()) {
                return "No projects are available in the system.";
            }

            return "Available Projects:\n" +
                    projects.stream()
                            .map(p -> String.format("- [ID: %s] %s", p.getIdentifier(), p.getName()))
                            .collect(Collectors.joining("\n"));

        } catch (Exception e) {
            Log.errorf(e, "GetAllProjectsTool: Unexpected error: %s", e.getMessage());
            return "ERROR: An unexpected error occurred while fetching projects: " + e.getMessage();
        }
    }
}