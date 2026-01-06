package org.jenga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "chat_memory", indexes = {
        @jakarta.persistence.Index(name = "idx_chat_memory_memory_id", columnList = "memoryId")
})
@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class ChatMemoryEntity extends PanacheEntity {

    @Column(nullable = false)
    public String memoryId;

    @Column(columnDefinition = "TEXT")
    public String messageJson;

    public String messageType; // "USER", "AI", "SYSTEM"
}
