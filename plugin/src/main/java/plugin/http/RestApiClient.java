package plugin.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiClient {
    
    public record Issue(int id, String name, String severity, String updatedAt) {
    };
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static HttpClient client = HttpClient.newHttpClient();
    
    static void setHttpClient(HttpClient httpClient) {
        client = httpClient;
    }
    
    public static List<Issue> getIssues() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/issues"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status != 200) {
            throw new Exception("Failed to fetch issues: HTTP " + status + " - " + response.body());
        }
        return OBJECT_MAPPER.readValue(response.body(), new TypeReference<List<Issue>>() {});
    }

}
