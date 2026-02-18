package org.jenga.service.mcpserver;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.memory.ChatMemory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jenga.model.ChatMemoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DatabaseChatMemoryStoreTest {

    @Inject
    DatabaseChatMemoryStore store;

    @Inject
    ChatMemoryProvider chatMemoryProvider;

    @AfterEach
    @Transactional
    void tearDown() {
        ChatMemoryEntity.deleteAll();
    }

    @Test
    void testUpdateAndGetMessages() {
        String memoryId = "test-session-1";
        UserMessage userMsg = UserMessage.from("Hello");
        AiMessage aiMsg = AiMessage.from("Hi there");
        List<ChatMessage> messages = List.of(userMsg, aiMsg);

        // Save messages
        store.updateMessages(memoryId, messages);

        // Retrieve messages
        List<ChatMessage> retrieved = store.getMessages(memoryId);

        assertEquals(2, retrieved.size());
        assertTrue(retrieved.get(0) instanceof UserMessage);
        assertEquals("Hello", extractText((UserMessage) retrieved.get(0)));
        assertTrue(retrieved.get(1) instanceof AiMessage);
        assertEquals("Hi there", ((AiMessage) retrieved.get(1)).text());
    }

    @Test
    void testUpdateMessages_OverwritesOldMessages() {
        String memoryId = "test-session-2";
        List<ChatMessage> initial = List.of(UserMessage.from("Old"));
        store.updateMessages(memoryId, initial);

        List<ChatMessage> updated = List.of(UserMessage.from("New"));
        store.updateMessages(memoryId, updated);

        List<ChatMessage> retrieved = store.getMessages(memoryId);
        assertEquals(1, retrieved.size());
        assertEquals("New", extractText((UserMessage) retrieved.get(0)));
    }

    private String extractText(UserMessage message) {
        return message.contents().stream()
                .filter(c -> c instanceof TextContent)
                .map(c -> ((TextContent) c).text())
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    @Test
    void testDeleteMessages() {
        String memoryId = "test-session-3";
        store.updateMessages(memoryId, List.of(UserMessage.from("msg")));

        store.deleteMessages(memoryId);

        List<ChatMessage> retrieved = store.getMessages(memoryId);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    void testGetMessages_ReturnsEmptyListWhenNoMessages() {
        List<ChatMessage> retrieved = store.getMessages("non-existent-id");
        assertTrue(retrieved.isEmpty());
    }

    @Test
    void testMultipleSequentialUpdates_FinalStateIsComplete() {
        String memoryId = "test-session-competing";
        UserMessage userMsg = UserMessage.from("Create a ticket");
        AiMessage toolCallMsg = AiMessage.from("[tool: getAllProjects]");
        AiMessage finalAiMsg = AiMessage.from("Done! Ticket created.");

        // Round-trip 1: LangChain4j persists [userMsg, toolCallMsg]
        List<ChatMessage> round1 = new ArrayList<>(List.of(userMsg, toolCallMsg));
        store.updateMessages(memoryId, round1);

        // Round-trip 2: LangChain4j persists [userMsg, toolCallMsg, finalAiMsg]
        List<ChatMessage> round2 = new ArrayList<>(List.of(userMsg, toolCallMsg, finalAiMsg));
        store.updateMessages(memoryId, round2);

        // The DB must contain the final complete state — not be empty
        List<ChatMessage> retrieved = store.getMessages(memoryId);
        assertEquals(3, retrieved.size(),
                "DB must contain the complete final conversation after multiple updateMessages calls");
        assertTrue(retrieved.get(0) instanceof UserMessage);
        assertTrue(retrieved.get(1) instanceof AiMessage);
        assertEquals("Done! Ticket created.", ((AiMessage) retrieved.get(2)).text());
    }

    @Test
    void testChatMemoryProvider_ReusesSameInstanceForSameMemoryId() {
        String memoryId = "test-provider-cache";

        ChatMemory first = chatMemoryProvider.get(memoryId);
        ChatMemory second = chatMemoryProvider.get(memoryId);

        assertSame(first, second,
                "ChatMemoryProvider must return the same cached instance for the same memoryId to prevent competing writes");
    }

    @Test
    void testChatMemoryProvider_ReturnsDifferentInstancesForDifferentMemoryIds() {
        ChatMemory forSession1 = chatMemoryProvider.get("session-A");
        ChatMemory forSession2 = chatMemoryProvider.get("session-B");

        assertNotSame(forSession1, forSession2,
                "ChatMemoryProvider must return different instances for different memoryIds");
    }
}
