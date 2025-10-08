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
    @Inject
    ProjectRepository projectRepository;
    
    @Transactional
    public void create(Project project) {
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
        existingProject.name = project.name;
        existingProject.description = project.description;
        projectRepository.persist(existingProject);
    }
    
    @Transactional
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
