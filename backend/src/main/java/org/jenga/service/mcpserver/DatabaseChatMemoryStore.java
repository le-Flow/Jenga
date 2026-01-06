package org.jenga.service.mcpserver;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.ChatMessageDeserializer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jenga.model.ChatMemoryEntity;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DatabaseChatMemoryStore implements ChatMemoryStore {

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMemoryEntity> entities = ChatMemoryEntity.list("memoryId", memoryId.toString());

        return entities.stream()
                .map(entity -> ChatMessageDeserializer.messageFromJson(entity.messageJson))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        deleteMessages(memoryId);

        for (ChatMessage message : messages) {
            ChatMemoryEntity entity = new ChatMemoryEntity();
            entity.memoryId = memoryId.toString();
            entity.messageJson = ChatMessageSerializer.messageToJson(message);
            entity.messageType = message.type().toString();
            entity.persist();
        }
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {
        ChatMemoryEntity.delete("memoryId", memoryId.toString());
    }
}
