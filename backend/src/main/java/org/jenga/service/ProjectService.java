package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.model.Project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Inject
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void create(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        projectRepository.persist(project);
    }

    public List<Project> findAll() {
        return projectRepository.findAll().list();
    }

    public Project findById(Long id) {
        return projectRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    @Transactional
    public void update(Long id, Project project) {
        Project existingProject = projectRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        projectRepository.persist(existingProject);
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
