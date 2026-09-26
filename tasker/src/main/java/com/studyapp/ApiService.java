package com.studyapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {

    private static final String API_URL =
            "https://dummyjson.com/quotes/random";

    public static String getMotivationalQuote() throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API Error: " + response.statusCode()
            );
        }

        String json = response.body();

        // JSON Parsing
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(json);

        String quote = root.get("quote").asText();
        String author = root.get("author").asText();

        return "\"" + quote + "\"\n\n- " + author;
    }
}