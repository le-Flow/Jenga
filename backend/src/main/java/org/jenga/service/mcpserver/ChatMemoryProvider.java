package org.jenga.service.mcpserver;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class ChatMemoryProvider implements dev.langchain4j.memory.chat.ChatMemoryProvider {

    private final DatabaseChatMemoryStore store;

    @Override
    public ChatMemory get(Object memoryId) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .maxMessages(20)
                .chatMemoryStore(store)
                .build();
    }
}