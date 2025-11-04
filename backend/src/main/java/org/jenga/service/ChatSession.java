package org.jenga.service;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.UUID;

@SessionScoped 
@RegisterForReflection
public class ChatSession implements Serializable {

    private final String conversationId;

    public ChatSession() {
        this.conversationId = UUID.randomUUID().toString();
    }

    public String getConversationId() {
        return conversationId;
    }
}