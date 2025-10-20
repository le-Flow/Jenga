package org.jenga.db;

import org.jenga.model.Project;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<Project> {
    public Project findByName(String projectName) {
        return find("name", projectName).firstResult();
    }
}
