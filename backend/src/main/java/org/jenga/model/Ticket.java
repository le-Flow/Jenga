package org.jenga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Ticket extends PanacheEntity {
    @ManyToOne
    public Project project;
    public String title;
    public String description;
}
