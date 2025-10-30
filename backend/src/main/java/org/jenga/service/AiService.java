package org.jenga.service;
import io.quarkiverse.langchain4j.RegisterAiService;

import org.jenga.tool.AskAboutTicketTool;

@RegisterAiService(tools = AskAboutTicketTool.class)
public interface AiService {
    String chat(String userMessage);
}