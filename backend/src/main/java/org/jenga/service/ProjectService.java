package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.model.Project;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;
import org.jenga.mapper.ProjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
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
        Project project = projectMapper.createProjectDTOToProject(createProjectDTO);

        projectRepository.persist(project);
    }

    public List<ProjectDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::projectToProjectDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO findByName(String projectName) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }
        return projectMapper.projectToProjectDTO(project);
    }

    @Transactional
    public void update(String projectName, ProjectDTO projectDTO) {
        Project existing = projectRepository.findByName(projectName);
        if (existing == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        existing.setName(projectDTO.getName());
        existing.setDescription(projectDTO.getDescription());

        projectRepository.persist(existing);
    }

    @Transactional
    public void delete(String projectName) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            throw new NotFoundException("Project not found with name: " + projectName);
        }

        projectRepository.delete(project);
    }
}
