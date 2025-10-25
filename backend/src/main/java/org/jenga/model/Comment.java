package org.jenga.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private String author;
    private String text;

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
