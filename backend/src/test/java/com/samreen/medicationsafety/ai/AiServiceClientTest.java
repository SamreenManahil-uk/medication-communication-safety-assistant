package com.samreen.medicationsafety.ai;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.samreen.medicationsafety.ai.service.AiServiceClient;
import com.samreen.medicationsafety.config.AiServiceConfig;
import com.samreen.medicationsafety.dto.CreateMedicationAnalysisRequest;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import tools.jackson.databind.ObjectMapper;

class AiServiceClientTest {

    @Test
    void shouldCallFastApiHybridAnalysis() {

        AiServiceConfig config = new AiServiceConfig();

        ReflectionTestUtils.setField(
                config,
                "baseUrl",
                "http://127.0.0.1:8000"
        );

        ObjectMapper objectMapper = new ObjectMapper();

        AiServiceClient client =
                new AiServiceClient(
                        config,
                        objectMapper
                );

        CreateMedicationAnalysisRequest request =
                new CreateMedicationAnalysisRequest();

        request.setOriginalText(
                "Paracetamol 500 mg take one tablet twice daily"
        );

        var response = client.analyse(request);

        assertNotNull(response);
    }
}
