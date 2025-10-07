package org.jenga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Project extends PanacheEntity {
    public String name;
    public String description;
}
