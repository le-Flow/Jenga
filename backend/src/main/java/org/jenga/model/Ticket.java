package org.jenga.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ticketNumber;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    private TicketSize size;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
        name = "ticket_labels",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<Label> labels;

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
