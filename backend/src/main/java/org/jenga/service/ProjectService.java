package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.model.Project;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;
import org.jenga.mapper.ProjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Inject
    public ProjectService(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public void create(CreateProjectDTO createProjectDTO) {
        String identifier = createProjectDTO.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BadRequestException("Project identifier cannot be empty");
        }

        if (identifier.length() > 10) {
            throw new BadRequestException("Project identifier cannot exceed 10 characters");
        }

        if (!identifier.equals(identifier.trim())) {
            throw new BadRequestException("Project identifier cannot have leading or trailing spaces");
        }

        if (identifier.contains(" ")) {
            throw new BadRequestException("Project identifier cannot contain spaces");
        }

        if (!identifier.matches("[a-zA-Z0-9]+")) {
            throw new BadRequestException("Project identifier must only contain letters and numbers (no special characters)");
        }

        Project existingProject = projectRepository.findById(identifier);
        if (existingProject != null) {
            throw new BadRequestException("Project already exists with this identifier");
        }

        Project project = projectMapper.createProjectDTOToProject(createProjectDTO);

        projectRepository.persist(project);
    }

    public List<ProjectDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO findById(String projectId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with ID: " + projectId);
        }
        return projectMapper.projectToProjectDTO(project);
    }

    @Transactional
    public void update(String projectId, ProjectDTO projectDTO) {
        Project existing = projectRepository.findById(projectId);
        if (existing == null) {
            throw new NotFoundException("Project not found with ID: " + projectId);
        }

        existing.setId(projectDTO.getIdentifier());
        existing.setName(projectDTO.getName());
        existing.setDescription(projectDTO.getDescription());

        projectRepository.persist(existing);
    }

    @Transactional
    public void delete(String projectId) {
        Project project = projectRepository.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found with ID: " + projectId);
        }

        projectRepository.delete(project);
    }
}
