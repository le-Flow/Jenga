package org.MCP_Server;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/ai")
public class AiResource {

    @Inject
    MyGeminiAssistant assistant;

    @GET
    @Path("/story")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStory(@QueryParam("animal") String animal, @QueryParam("setting") String setting) {
        if (animal == null || setting == null) {
            return "Please provide an animal and a setting (e.g., /ai/story?animal=cat&setting=space)";
        }
        return assistant.generateStory(animal, setting);
    }

    @GET
    @Path("/chat")
    @Produces(MediaType.TEXT_PLAIN)
    public String chatWithGemini(@QueryParam("message") String message) {
        if (message == null) {
            return "Please provide a message (e.g., /ai/chat?message=What is the weather in London?)";
        }
        return assistant.chat(message);
    }
}