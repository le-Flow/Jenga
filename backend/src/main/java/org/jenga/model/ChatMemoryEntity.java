package org.jenga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chat_memory", indexes = {
        @Index(name = "idx_chat_memory_memory_id", columnList = "memoryId")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMemoryEntity extends PanacheEntity {

    @Column(nullable = false)
    public String memoryId;

    @Column(columnDefinition = "TEXT")
    public String messageJson;

    @Enumerated(EnumType.STRING)
    public MessageType messageType; // "USER", "AI", "SYSTEM", "TOOL"
}
