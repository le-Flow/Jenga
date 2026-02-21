package org.jenga.service.mcpserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jenga.dto.mcpserver.WebSearchResponseDTO;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(GoogleSearchApiTest.WireMockExtensions.class)
public class GoogleSearchApiTest {

    @Inject
    @RestClient
    GoogleSearchApi googleSearchApi;

    @Test
    void testSearch_Success() {
        WebSearchResponseDTO response = googleSearchApi.search("dummyKey", "dummyCx", "Quarkus");

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());
        assertEquals("Quarkus - Supersonic Subatomic Java", response.getItems().get(0).getTitle());
        assertEquals("https://quarkus.io/", response.getItems().get(0).getLink());
    }

    public static class WireMockExtensions implements QuarkusTestResourceLifecycleManager {

        private WireMockServer wireMockServer;

        @Override
        public Map<String, String> start() {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
            wireMockServer.start();

            // Stubbing the Google Custom Search API
            // Path: /customsearch/v1
            // Query params: key, cx, q

            String jsonResponse = """
                        {
                            "items": [
                                {
                                    "title": "Quarkus - Supersonic Subatomic Java",
                                    "link": "https://quarkus.io/",
                                    "snippet": "Quarkus is a Cloud Native, (Linux) Container First framework..."
                                }
                            ]
                        }
                    """;

            wireMockServer.stubFor(get(urlPathEqualTo("/customsearch/v1"))
                    .withQueryParam("key", matching(".*"))
                    .withQueryParam("cx", matching(".*"))
                    .withQueryParam("q", equalTo("Quarkus"))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(jsonResponse)));

            // Override the config key 'google-search-api' to point to WireMock
            return Collections.singletonMap("quarkus.rest-client.google-search-api.url", wireMockServer.baseUrl());
        }

        @Override
        public void stop() {
            if (wireMockServer != null) {
                wireMockServer.stop();
            }
        }
    }
}
