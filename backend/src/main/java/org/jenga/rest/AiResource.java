package org.jenga.rest;

import jakarta.inject.Inject; 
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jenga.dto.MCP_Server.ChatRequestDTO;
import org.jenga.dto.MCP_Server.ChatResponseDTO;
import org.jenga.service.MCP_Server.AiService;
import org.jenga.service.MCP_Server.ChatSession;

@Path("/api/ai")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {

    @Inject
    AiService assistant; 
    
    @Inject
    ChatSession chatSession; 

    @POST
    @Path("/chat")
    public ChatResponseDTO chat(ChatRequestDTO request) {
        
        String conversationId = chatSession.getConversationId();
        String aiResponse = assistant.chat(conversationId, request.getMessage());

        return new ChatResponseDTO(aiResponse);
    }
}