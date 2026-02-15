package org.jenga.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.jenga.db.UserRepository;
import org.jenga.dto.mcpserver.ChatRequestDTO;
import org.jenga.model.User;
import org.jenga.model.ChatSessionEntity;
import org.jenga.service.mcpserver.AiService;
import org.jenga.service.mcpserver.DatabaseChatMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import org.junit.jupiter.api.AfterEach;
import jakarta.transaction.UserTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class AiResourceTest {

    @InjectMock
    AiService aiService;

    @InjectMock
    DatabaseChatMemoryStore memoryStore;

    @Inject
    UserRepository userRepository;

    @Inject
    UserTransaction transaction;

    @BeforeEach
    void setup() throws Exception {
        transaction.begin();
        // make sure test user exists
        if (userRepository.findByUsername("testuser") == null) {
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            userRepository.persist(user);
        }
        transaction.commit();
    }

    @AfterEach
    void tearDown() throws Exception {
        transaction.begin();
        ChatSessionEntity.deleteAll();
        transaction.commit();
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testChatEndpoint_Success() {
        String mockResponse = "Hello, how can I help?";
        Mockito.when(aiService.chat(anyString(), eq("Hi")))
                .thenReturn(mockResponse);

        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Hi");
        request.setCurrentUser("testuser");
        request.setCurrentProjectID(1L);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
                .body("response", equalTo(mockResponse))
                .body("conversationId", notNullValue());
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testChatEndpoint_NullPointer_ErrorHandling() {
        // NullPointerException (e.g. tool failure)
        Mockito.when(aiService.chat(anyString(), anyString()))
                .thenThrow(new NullPointerException("Tool failure simulation"));

        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Break it");
        request.setCurrentUser("testuser");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
                .body("response", containsString("encountered an error processing that request"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testChatEndpoint_GeneralException_ErrorHandling() {
        Mockito.when(aiService.chat(anyString(), anyString()))
                .thenThrow(new RuntimeException("Something bad happened"));

        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Crash");
        request.setCurrentUser("testuser");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
                .body("response", containsString("An unexpected error occurred"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testGetSessions() throws Exception {
        transaction.begin();
        User user = userRepository.findByUsername("testuser");

        ChatSessionEntity session = new ChatSessionEntity();
        session.sessionId = "session-123";
        session.user = user;
        session.title = "Test Session";
        session.startedAt = LocalDateTime.now();
        session.persist();
        transaction.commit();

        given()
                .queryParam("userId", "testuser")
                .when()
                .get("/api/ai/sessions")
                .then()
                .statusCode(200)
                .body("[0].sessionId", equalTo("session-123"))
                .body("[0].title", equalTo("Test Session"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testGetSessionMessages() {
        String sessionId = "session-123";

        Mockito.when(memoryStore.getMessages(sessionId))
                .thenReturn(List.of(
                        new UserMessage("Hello"),
                        new AiMessage("Hi there")));

        given()
                .pathParam("sessionId", sessionId)
                .when()
                .get("/api/ai/sessions/{sessionId}/messages")
                .then()
                .statusCode(200)
                .body("[0].type", equalTo("USER"))
                .body("[0].content", equalTo("Hello"))
                .body("[1].type", equalTo("AI"))
                .body("[1].content", equalTo("Hi there"));
    }
}
