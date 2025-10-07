package org.jenga.service;

import org.jenga.db.ProjectRepository;
import org.jenga.model.Project;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class ProjectService {
    @Inject
    ProjectRepository projectRepository;

    public void create(Project project) {
    }

    public List<Project> findAll() {
        return projectRepository.findAll().list();
    }

    public Project findById(Long id) {
        return projectRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    public void update(Long id, Project project) {
    }

    public void delete(Long id) {
    }
}
