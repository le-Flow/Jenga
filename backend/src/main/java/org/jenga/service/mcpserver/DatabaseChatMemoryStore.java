package org.jenga.service.mcpserver;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import io.quarkus.logging.Log;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jenga.model.ChatMemoryEntity;
import org.jenga.model.MessageType;

import java.util.List;

@ApplicationScoped
public class DatabaseChatMemoryStore implements ChatMemoryStore {

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Log.debug("Retrieving messages for memoryId: " + memoryId);
        List<ChatMemoryEntity> entities = ChatMemoryEntity.list("memoryId", memoryId.toString());

        return entities.stream()
                .map(entity -> ChatMessageDeserializer.messageFromJson(entity.messageJson))
                .toList();
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Log.debug("Updating messages for memoryId: " + memoryId + ". Count: " + messages.size());
        deleteMessages(memoryId);

        for (ChatMessage message : messages) {
            ChatMemoryEntity entity = new ChatMemoryEntity();
            entity.memoryId = memoryId.toString();
            entity.messageJson = ChatMessageSerializer.messageToJson(message);
            entity.messageType = MessageType.valueOf(message.type().name());
            entity.persist();
        }
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {
        Log.debug("Deleting messages for memoryId: " + memoryId);
        ChatMemoryEntity.delete("memoryId", memoryId.toString());
    }
}
