package org.jenga.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.jenga.dto.mcpserver.ChatRequestDTO;
import org.jenga.service.mcpserver.AiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        // Simulate a NullPointerException from the service (e.g. tool failure)
        Mockito.when(aiService.chat(anyString(), anyString()))
                .thenThrow(new NullPointerException("Tool failure simulation"));

        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Break it");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/ai/chat")
                .then()
                .statusCode(200) // The resource catches exceptions and returns 200 with error msg
                .body("response", containsString("encountered an error processing that request"));
    }

    @Test
    @TestSecurity(user = "testuser", roles = "user")
    void testChatEndpoint_GeneralException_ErrorHandling() {
        Mockito.when(aiService.chat(anyString(), anyString()))
                .thenThrow(new RuntimeException("Something bad happened"));

        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Crash");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/ai/chat")
                .then()
                .statusCode(200)
                .body("response", containsString("An unexpected error occurred"));
    }
}
