package org.jenga.tool;

import org.jenga.dto.mcpserver.SearchResultItemDTO;
import org.jenga.dto.mcpserver.WebSearchResponseDTO;
import org.jenga.service.mcpserver.GoogleSearchApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class WebSearchToolUnitTest {

    @Mock
    GoogleSearchApi searchApi;

    @InjectMocks
    WebSearchTool webSearchTool;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Use reflection to set private Config fields
        setConfigField("apiKey", "test-key");
        setConfigField("searchEngineId", "test-cx");
    }

    private void setConfigField(String fieldName, String value) throws Exception {
        Field field = WebSearchTool.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(webSearchTool, value);
    }

    @Test
    void testSearchWeb_Success() {
        WebSearchResponseDTO mockResponse = new WebSearchResponseDTO();
        SearchResultItemDTO item = new SearchResultItemDTO();
        item.setTitle("Test Title");
        item.setSnippet("Test Snippet");
        item.setLink("http://example.com");
        mockResponse.setItems(List.of(item));

        when(searchApi.search(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        List<String> results = webSearchTool.searchWeb("test query");

        assertEquals(1, results.size());
        String res = results.get(0);
        assertTrue(res.contains("Test Title"));
        assertTrue(res.contains("Test Snippet"));
        assertTrue(res.contains("http://example.com"));
    }

    @Test
    void testSearchWeb_MissingConfig() throws Exception {
        setConfigField("apiKey", null);

        List<String> results = webSearchTool.searchWeb("query");

        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("not configured"));
    }

    @Test
    void testSearchWeb_NoResults() {
        WebSearchResponseDTO mockResponse = new WebSearchResponseDTO();
        mockResponse.setItems(Collections.emptyList());

        when(searchApi.search(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        List<String> results = webSearchTool.searchWeb("weird query");

        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("No results found"));
    }

    @Test
    void testSearchWeb_ApiError() {
        when(searchApi.search(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("API Down"));

        List<String> results = webSearchTool.searchWeb("query");

        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("Error performing search"));
        assertTrue(results.get(0).contains("API Down"));
    }
}
