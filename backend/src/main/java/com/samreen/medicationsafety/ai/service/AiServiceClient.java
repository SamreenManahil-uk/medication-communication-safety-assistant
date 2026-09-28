package com.samreen.medicationsafety.ai.service;

import com.samreen.medicationsafety.ai.dto.AiAnalysisResponse;
import com.samreen.medicationsafety.config.AiServiceConfig;
import com.samreen.medicationsafety.dto.CreateMedicationAnalysisRequest;

import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class AiServiceClient {

    private final AiServiceConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AiServiceClient(
            AiServiceConfig config,
            ObjectMapper objectMapper
    ) {
        this.config = config;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public AiAnalysisResponse analyse(
            CreateMedicationAnalysisRequest request
    ) {

        try {

            String json = objectMapper.writeValueAsString(
                    java.util.Map.of(
                            "text",
                            request.getOriginalText()
                    )
            );

            byte[] jsonBytes =
                    json.getBytes(StandardCharsets.UTF_8);

            String url =
                    config.getBaseUrl()
                            + "/api/v1/hybrid-analyze";

            System.out.println("========================================");
            System.out.println("AI JAVA REQUEST DEBUG");
            System.out.println("URL: " + url);
            System.out.println("JSON: " + json);
            System.out.println("JSON LENGTH: " + jsonBytes.length);
            System.out.println("HTTP VERSION: HTTP/1.1");
            System.out.println("========================================");

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .version(HttpClient.Version.HTTP_1_1)
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofByteArray(
                                            jsonBytes
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString(
                                    StandardCharsets.UTF_8
                            )
                    );

            System.out.println("========================================");
            System.out.println("AI JAVA RESPONSE DEBUG");
            System.out.println("STATUS: " + response.statusCode());
            System.out.println("BODY: " + response.body());
            System.out.println("========================================");

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "AI service returned HTTP "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return objectMapper.readValue(
                    response.body(),
                    AiAnalysisResponse.class
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to call medication AI service",
                    e
            );
        }
    }
}
