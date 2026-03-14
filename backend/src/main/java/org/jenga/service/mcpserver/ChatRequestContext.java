package org.jenga.service.mcpserver;

import jakarta.enterprise.context.RequestScoped;
import lombok.Data;

@RequestScoped
@Data
public class ChatRequestContext {
    private String currentUser;
    private String currentProjectID;
    private Long currentTicketID;
}