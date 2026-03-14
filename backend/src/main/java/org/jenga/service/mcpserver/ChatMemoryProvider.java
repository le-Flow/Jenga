package org.jenga.service.mcpserver;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChatMemoryProvider implements dev.langchain4j.memory.chat.ChatMemoryProvider {

    private final ConcurrentHashMap<Object, ChatMemory> memoryCache = new ConcurrentHashMap<>();

    @Override
    public ChatMemory get(Object memoryId) {
        return memoryCache.computeIfAbsent(memoryId, id -> MessageWindowChatMemory.builder()
                .id(id)
                .maxMessages(20)
                .build());
    }
}