package org.jenga.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "projects")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    private String name;
    private String description;
    
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @PrePersist
    public void onCreate() {
        createDate = LocalDateTime.now();
        modifyDate = createDate;
    }

    @PreUpdate
    public void onUpdate() {
        modifyDate = LocalDateTime.now();
    }
}
