package org.jenga.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import io.quarkus.logging.Log;

import java.util.UUID;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

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
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;

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

        Log.infof("Processing chat request for conversationId: %s, userId: %s, projectId: %s", conversationId,
                request.getCurrentUser(), request.getCurrentProjectID());

        requestContext.setCurrentUser(request.getCurrentUser());
        requestContext.setCurrentProjectID(request.getCurrentProjectID());
        requestContext.setCurrentTicketID(request.getCurrentTicketID());

        List<dev.langchain4j.data.message.ChatMessage> updatedMessages = null;

        try {
            // Manually persist user message BEFORE calling AI service
            List<dev.langchain4j.data.message.ChatMessage> existingMessages = memoryStore.getMessages(conversationId);
            updatedMessages = new java.util.ArrayList<>(existingMessages);
            updatedMessages.add(dev.langchain4j.data.message.UserMessage.from(request.getMessage()));
            memoryStore.updateMessages(conversationId, updatedMessages);
            Log.infof("Manually persisted UserMessage for conversationId: %s", conversationId);

            String aiResponse = assistant.chat(conversationId, request.getMessage());

            // Manually persist AI response AFTER receiving it
            updatedMessages.add(dev.langchain4j.data.message.AiMessage.from(aiResponse));
            memoryStore.updateMessages(conversationId, updatedMessages);
            Log.infof("Manually persisted AiMessage for conversationId: %s", conversationId);

            return new ChatResponseDTO(aiResponse, conversationId);

        } catch (NullPointerException e) {
            Log.errorf(e, "NPE during chat processing for conversationId: %s", conversationId);
            String errorMsg = "I encountered an error processing that request. This is usually due to a tool execution issue. Please try rephrasing your request or try again.";

            // Persist error message as AI response
            if (updatedMessages != null) {
                updatedMessages.add(dev.langchain4j.data.message.AiMessage.from(errorMsg));
                memoryStore.updateMessages(conversationId, updatedMessages);
                Log.infof("Manually persisted error AiMessage for conversationId: %s", conversationId);
            }

            return new ChatResponseDTO(errorMsg, conversationId);
        } catch (Exception e) {
            Log.errorf(e, "Unexpected error during chat processing for conversationId: %s", conversationId);
            String errorMsg = "An unexpected error occurred: " + e.getMessage();

            // Persist error message as AI response
            if (updatedMessages != null) {
                updatedMessages.add(dev.langchain4j.data.message.AiMessage.from(errorMsg));
                memoryStore.updateMessages(conversationId, updatedMessages);
                Log.infof("Manually persisted error AiMessage for conversationId: %s", conversationId);
            }

            return new ChatResponseDTO(errorMsg, conversationId);
        }
    }

    @Transactional
    void ensureSessionExists(String conversationId, String userId, String projectId, String initialMessage) {
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
            Log.infof("Created new chat session: %s", conversationId);
        }
    }

    @Context
    SecurityContext securityContext;

    @GET
    @Path("/sessions")
    public List<ChatSessionDTO> getSessions() {
        String userId = securityContext.getUserPrincipal().getName();
        Log.debugf("Fetching chat sessions for userId: %s", userId);

        List<ChatSessionEntity> sessions = ChatSessionEntity.find("user.username = ?1 ORDER BY startedAt DESC", userId)
                .list();
        return sessions.stream()
                .map(s -> new ChatSessionDTO(s.sessionId, s.title, s.startedAt))
                .toList();
    }

    @GET
    @Path("/sessions/{sessionId}/messages")
    public List<ChatMessageDTO> getSessionMessages(@PathParam("sessionId") String sessionId) {
        Log.infof("Fetching messages for sessionId: %s", sessionId);
        List<ChatMessage> messages = memoryStore.getMessages(sessionId);
        Log.infof("Found %d messages for sessionId: %s", messages.size(), sessionId);

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
