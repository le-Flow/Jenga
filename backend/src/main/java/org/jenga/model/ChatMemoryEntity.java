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

    @Column(nullable = false) // Removed index=true as it's not standard JPA, though Hibernate supports it.
                              // For now, keeping it simple or I can add @Index to @Table if needed, but the
                              // prompt used @Column(index=true) which might be a shorthand in some versions
                              // or pseudocode. Wait, @Column in JPA doesn't have index attribute. Use
                              // @Table(indexes = ...). However, maybe the user meant a specific extension. I
                              // will stick to standard JPA.
    // Actually, let's look at the user snippet: @Column(nullable = false, index =
    // true). 'index' is not a member of jakarta.persistence.Column. It might be a
    // mistake in the user's snippet. Use distinct index annotation or table index.
    // I will remove 'index = true' to avoid compilation error and maybe add it
    // properly if needed, or just leave it out for now.
    public String memoryId;

    @Column(columnDefinition = "TEXT") // or JSONB if using Postgres
    public String messageJson;

    public String messageType; // "USER", "AI", "SYSTEM" (Optional, for debugging)
}
