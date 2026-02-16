package org.jenga.service;

import java.util.List;

import org.jenga.dto.ProjectRequestDTO;
import org.jenga.dto.ProjectResponseDTO;
import org.jenga.dto.LabelDTO;

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
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
class ProjectServiceTest{
    @Inject
    ProjectService service;

    private static final String PROJECT_IDENTIFIER = "testproject";
    private static final String PROJECT_NAME = "Test Project";
    private static final String PROJECT_DESCRIPTION = "Sample Test Project";

    @Test
    @TestTransaction
    void testCreateProject() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        ProjectResponseDTO response = service.create(dto);

        assertEquals(response.getIdentifier(), PROJECT_IDENTIFIER);
        assertEquals(response.getName(), PROJECT_NAME);
        assertEquals(response.getDescription(), PROJECT_DESCRIPTION);
    }

    @Test
    @TestTransaction
    void testCreateProjectAlreadyExistingIdentifier() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        ProjectResponseDTO response = service.create(dto);

        assertEquals(response.getIdentifier(), PROJECT_IDENTIFIER);
        assertEquals(response.getName(), PROJECT_NAME);
        assertEquals(response.getDescription(), PROJECT_DESCRIPTION);

        assertThrows(BadRequestException.class, () -> service.create(dto));
    }

    @Test
    @TestTransaction
    void testFindallProjects() { 
        String id1 = "project1";
        String name1 = "Project 1";
        String description1= "Description 1";

        String id2 = "project2";
        String name2 = "Project 2";
        String description2 = "Description 2";

        ProjectRequestDTO project1 = new ProjectRequestDTO();
        project1.setIdentifier(id1);
        project1.setName(name1);
        project1.setDescription(description1);

        ProjectRequestDTO project2 = new ProjectRequestDTO();
        project2.setIdentifier(id2);
        project2.setName(name2);
        project2.setDescription(description2);

        service.create(project1);
        service.create(project2);

        List<ProjectResponseDTO> projects = service.findAll();

        assertTrue(projects.size() >= 2);

        ProjectResponseDTO response1 = projects.stream()
            .filter(p -> p.getIdentifier().equals(id1))
            .findFirst()
            .orElseThrow();

        assertEquals(name1, response1.getName());
        assertEquals(description1, response1.getDescription());

        ProjectResponseDTO response2 = projects.stream()
            .filter(p -> p.getIdentifier().equals(id2))
            .findFirst()
            .orElseThrow();
        
        assertEquals(name2, response2.getName());
        assertEquals(description2, response2.getDescription());
    }

    @Test
    @TestTransaction
    void testFindProjectByIdExisting() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        service.create(dto);

        ProjectResponseDTO response = service.findById(PROJECT_IDENTIFIER);

        assertEquals(response.getIdentifier(), PROJECT_IDENTIFIER);
        assertEquals(response.getName(), PROJECT_NAME);
        assertEquals(response.getDescription(), PROJECT_DESCRIPTION);
    }

    @Test
    @TestTransaction
    void testFindProjectByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> service.findById(PROJECT_IDENTIFIER));
    }

    @Test
    @TestTransaction
    void testUpdateProject() {
        String new_name = "New name";
        String new_description = "New Descrption";

        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        service.create(dto);

        dto.setName(new_name);
        dto.setDescription(new_description);
        service.update(PROJECT_IDENTIFIER, dto);

        ProjectResponseDTO response = service.findById(PROJECT_IDENTIFIER);
        assertEquals(response.getName(), new_name);
        assertEquals(response.getDescription(), new_description);
    }

    @Test
    @TestTransaction
    void testDeleteExistingProject() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        ProjectResponseDTO response = service.create(dto);

        assertNotNull(response);
        assertEquals(response.getIdentifier(), PROJECT_IDENTIFIER);

        service.delete(PROJECT_IDENTIFIER);

        assertThrows(NotFoundException.class, () -> service.findById(PROJECT_IDENTIFIER));
    }

    @Test
    @TestTransaction
    void testDeleteNonExistingProject() {
        assertThrows(NotFoundException.class, () -> service.findById(PROJECT_IDENTIFIER));
    }

    @Test
    @TestTransaction
    void testCreateLabelsForProject() {
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setIdentifier(PROJECT_IDENTIFIER);
        dto.setName(PROJECT_NAME);
        dto.setDescription(PROJECT_DESCRIPTION);
        
        service.create(dto);

        List<LabelDTO> labels = service.getAllLabels(PROJECT_IDENTIFIER);
        assertEquals(0, labels.size());

        LabelDTO label = new LabelDTO();
        label.setName("testing");
        label.setColor("#123456");
        service.createLabel(PROJECT_IDENTIFIER, label);

        labels = service.getAllLabels(PROJECT_IDENTIFIER);
        assertEquals(1, labels.size());
        assertEquals(label.getName(), labels.get(0).getName());

        service.deleteLabel(PROJECT_IDENTIFIER, label.getName());

        labels = service.getAllLabels(PROJECT_IDENTIFIER);
        assertEquals(0, labels.size());
    }
    
    @Test
    @TestTransaction
    void testCreateLabelInvalidProject() {
        LabelDTO label = new LabelDTO();
        label.setName("testing");
        label.setColor("#123456");

        assertThrows(NotFoundException.class, () -> service.createLabel("invalid", label));
    }
}
