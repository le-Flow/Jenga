package org.jenga.db;

import java.util.List;

import org.jenga.model.Label;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LabelRepository implements PanacheRepository<Label> {
    public List<Label> findByProjectId(String projectId) {
        return find("project.id", projectId).list();
    }

    public Label findByProjectIdAndLabelName(String projectId, String labelName) {
        return find("project.id = ?1 and name = ?2", projectId, labelName).firstResult();
    }

    public List<Label> findByProjectIdAndNames(String projectId, List<String> labelNames) {
    return find("project.id = ?1 and name in ?2", projectId, labelNames).list();
}
}
