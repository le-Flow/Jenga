package org.jenga.service.MCP_Server;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jenga.dto.MCP_Server.WebSearchResponseDTO;

import jakarta.ws.rs.*;

@RegisterRestClient(configKey = "google.api")
@Path("/v1")
public interface GoogleSearchApi {

    @GET
    WebSearchResponseDTO search(
            @QueryParam("key") String apiKey,
            @QueryParam("cx") String searchEngineId,
            @QueryParam("q") String query
    );
}