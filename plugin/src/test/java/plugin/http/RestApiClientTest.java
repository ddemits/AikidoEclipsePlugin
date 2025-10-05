package plugin.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;


public class RestApiClientTest {

    @Test
    void testGetIssuesStatus200() throws Exception {
        // TODO: fix warnings about generic type inferring
        String json = Files.readString(Paths.get("src/test/resources/issues.json"));
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(json);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        RestApiClient.setHttpClient(mockClient);
        
        List<RestApiClient.Issue> issues = RestApiClient.getIssues();

        RestApiClient.Issue issue1 = issues.get(0);
        assertEquals(1, issue1.id());
        assertEquals("Outdated dependency", issue1.name());
        assertEquals("medium", issue1.severity());
        assertEquals("2025-08-20T10:15:00Z", issue1.updatedAt());

        RestApiClient.Issue issue2 = issues.get(1);
        assertEquals(2, issue2.id());
        assertEquals("SQL injection risk", issue2.name());
        assertEquals("high", issue2.severity());
        assertEquals("2025-08-21T08:03:00Z", issue2.updatedAt());

        RestApiClient.Issue issue3 = issues.get(2);
        assertEquals(3, issue3.id());
        assertEquals("Weak TLS settings", issue3.name());
        assertEquals("low", issue3.severity());
        assertEquals("2025-08-22T12:45:00Z", issue3.updatedAt());
    }
    
    @Test
    void testGetIssuesNon200() throws Exception {
    	// TODO: fix warnings about generic type inferring
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        RestApiClient.setHttpClient(mockClient);

        Exception exception = assertThrows(Exception.class, () -> {
            RestApiClient.getIssues();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch issues"));
        assertTrue(exception.getMessage().contains("500"));
        assertTrue(exception.getMessage().contains("Internal Server Error"));
    }
    
}
