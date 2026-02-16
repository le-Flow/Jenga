package org.jenga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Data
@EqualsAndHashCode(callSuper = false)
public class ChatSessionEntity extends PanacheEntityBase {

    @Id
    public String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public Long projectId;
    public String title;
    public LocalDateTime startedAt;

    public static ChatSessionEntity findBySessionId(String sessionId) {
        return find("sessionId", sessionId).firstResult();
    }
}
