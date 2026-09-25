package com.studyflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class ApiService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private ApiService() { }

    public static CompletableFuture<Quote> fetchQuoteAsync() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dummyjson.com/quotes/random"))
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new RuntimeException("API returned HTTP " + response.statusCode());
                    }
                    return parseQuote(response.body());
                });
    }

    private static Quote parseQuote(String json) {
        try {
            JsonNode root = MAPPER.readTree(json);
            return new Quote(root.path("quote").asText("Stay focused on one useful step at a time."),
                    root.path("author").asText("StudyFlow"));
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON response", e);
        }
    }

    public record Quote(String text, String author) { }
}
