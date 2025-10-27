package org.jenga.MCP_Server;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = WeatherTool.class) // Explicitly register the tool
public interface MyGeminiAssistant {

    @UserMessage("Tell me a short story about a {animal} in a {setting}.")
    String generateStory(String animal, String setting);

    // This method can utilize the registered WeatherTool
    String chat(String userMessage);
}