package org.jenga.dto.mcpserver;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String message;
    private String conversationId;

    private String currentUser;
    private String currentProjectID;
    private Long currentTicketID;
}
