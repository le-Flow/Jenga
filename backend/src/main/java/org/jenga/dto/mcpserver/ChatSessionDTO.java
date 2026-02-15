package org.jenga.dto.mcpserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionDTO {
    private String sessionId;
    private String title;
    private LocalDateTime startedAt;
}
