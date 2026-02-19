package org.jenga.tool;

import org.jenga.dto.ProjectResponseDTO;
import org.jenga.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GetAllProjectsToolUnitTest {

    @Mock
    ProjectService projectService;

    @InjectMocks
    GetAllProjectsTool getAllProjectsTool;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProjects_WithProjects() {
        ProjectResponseDTO p1 = new ProjectResponseDTO();
        p1.setIdentifier("P1");
        p1.setName("Project One");

        ProjectResponseDTO p2 = new ProjectResponseDTO();
        p2.setIdentifier("P2");
        p2.setName("Project Two");

        when(projectService.findAll()).thenReturn(List.of(p1, p2));

        String result = getAllProjectsTool.getAllProjects();

        assertTrue(result.contains("Available Projects:"));
        assertTrue(result.contains("- [ID: P1] Project One"));
        assertTrue(result.contains("- [ID: P2] Project Two"));
    }

    @Test
    void testGetAllProjects_Empty() {
        when(projectService.findAll()).thenReturn(Collections.emptyList());

        String result = getAllProjectsTool.getAllProjects();

        assertEquals("No projects are available in the system.", result);
    }

    @Test
    void testGetAllProjects_Error() {
        when(projectService.findAll()).thenThrow(new RuntimeException("DB Error"));

        String result = getAllProjectsTool.getAllProjects();

        assertTrue(result.startsWith("ERROR: An unexpected error occurred"));
        assertTrue(result.contains("DB Error"));
    }
}
