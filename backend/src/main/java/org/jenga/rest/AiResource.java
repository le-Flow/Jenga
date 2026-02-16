package org.jenga.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.jenga.dto.mcpserver.ChatRequestDTO;
import org.jenga.dto.mcpserver.ChatResponseDTO;
import org.jenga.dto.mcpserver.ChatMessageDTO;
import org.jenga.dto.mcpserver.ChatSessionDTO;
import org.jenga.model.ChatSessionEntity;
import org.jenga.model.User;
import org.jenga.db.UserRepository;
import org.jenga.service.mcpserver.AiService;
import org.jenga.service.mcpserver.ChatRequestContext;
import org.jenga.service.mcpserver.DatabaseChatMemoryStore;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/api/ai")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class AiResource {

    private final AiService assistant;
    private final ChatRequestContext requestContext;
    private final DatabaseChatMemoryStore memoryStore;
    private final UserRepository userRepository;

    @POST
    @Path("/chat")
    public ChatResponseDTO chat(ChatRequestDTO request) {
        String conversationId = request.getConversationId();

        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }

        ensureSessionExists(conversationId, request.getCurrentUser(), request.getCurrentProjectID(),
                request.getMessage());

        requestContext.setCurrentUser(request.getCurrentUser());
        requestContext.setCurrentProjectID(request.getCurrentProjectID());
        requestContext.setCurrentTicketID(request.getCurrentTicketID());

        try {
            String aiResponse = assistant.chat(conversationId, request.getMessage());
            return new ChatResponseDTO(aiResponse, conversationId);

        } catch (NullPointerException e) {
            String errorMsg = "I encountered an error processing that request. This is usually due to a tool execution issue. Please try rephrasing your request or try again.";
            return new ChatResponseDTO(errorMsg, conversationId);
        } catch (Exception e) {
            String errorMsg = "An unexpected error occurred: " + e.getMessage();
            return new ChatResponseDTO(errorMsg, conversationId);
        }
    }

    @Transactional
    void ensureSessionExists(String conversationId, String userId, Long projectId, String initialMessage) {
        if (ChatSessionEntity.findBySessionId(conversationId) == null) {
            ChatSessionEntity session = new ChatSessionEntity();
            session.sessionId = conversationId;

            if (userId != null) {
                User user = userRepository.findByUsername(userId);
                session.user = user;
            }

            session.projectId = projectId;
            session.startedAt = LocalDateTime.now();

            // Generate a simple title from the first message
            String title = initialMessage;
            if (title != null && title.length() > 30) {
                title = title.substring(0, 30) + "...";
            } else if (title == null || title.isBlank()) {
                title = "New Chat";
            }
            session.title = title;

            session.persist();
        }
    }

    @GET
    @Path("/sessions")
    public List<ChatSessionDTO> getSessions(@QueryParam("userId") String userId) {
        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        List<ChatSessionEntity> sessions = ChatSessionEntity.find("user.username = ?1 ORDER BY startedAt DESC", userId)
                .list();
        return sessions.stream()
                .map(s -> new ChatSessionDTO(s.sessionId, s.title, s.startedAt))
                .toList();
    }

    @GET
    @Path("/sessions/{sessionId}/messages")
    public List<ChatMessageDTO> getSessionMessages(@PathParam("sessionId") String sessionId) {
        List<ChatMessage> messages = memoryStore.getMessages(sessionId);

        return messages.stream()
                .map(this::mapToDTO).toList();
    }

    private ChatMessageDTO mapToDTO(ChatMessage message) {
        String type;
        String content;

        if (message instanceof UserMessage) {
            type = "USER";
            content = ((UserMessage) message).contents().stream()
                    .filter(c -> c instanceof TextContent)
                    .map(c -> ((TextContent) c).text())
                    .collect(Collectors.joining("\n"));
        } else if (message instanceof AiMessage) {
            type = "AI";
            content = ((AiMessage) message).text();
        } else if (message instanceof SystemMessage) {
            type = "SYSTEM";
            content = ((SystemMessage) message).text();
        } else {
            type = "TOOL";
            content = message.toString();
        }

        return new ChatMessageDTO(type, content);
    }
}
