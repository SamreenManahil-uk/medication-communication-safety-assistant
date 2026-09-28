package com.samreen.medicationsafety.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.samreen.medicationsafety.dto.CreateMedicationAnalysisRequest;
import com.samreen.medicationsafety.dto.MedicationAnalysisResponse;
import com.samreen.medicationsafety.entity.MedicationAnalysis;
import com.samreen.medicationsafety.mapper.MedicationAnalysisMapper;
import com.samreen.medicationsafety.service.MedicationAnalysisService;

@ExtendWith(MockitoExtension.class)
class MedicationAnalysisControllerTest {

    @Mock
    private MedicationAnalysisService service;

    @Mock
    private MedicationAnalysisMapper mapper;

    private MedicationAnalysisController controller;

    @BeforeEach
    void setUp() {
        controller = new MedicationAnalysisController(
                service,
                mapper
        );
    }

    @Test
    void shouldReturnAnalysisById() {

        MedicationAnalysis analysis =
                new MedicationAnalysis();

        analysis.setOriginalText(
                "Take one tablet twice daily after food"
        );
        analysis.setMedicationName("Demo Medicine");

        MedicationAnalysisResponse response =
                new MedicationAnalysisResponse();

        response.setId(1L);
        response.setOriginalText(
                "Take one tablet twice daily after food"
        );
        response.setMedicationName("Demo Medicine");
        response.setStrength("500 mg");
        response.setDosage("1 tablet");
        response.setFrequency("Twice daily");
        response.setRoute("Oral");
        response.setAmbiguityDetected(false);
        response.setConfidenceScore(0.95);
        response.setCreatedAt(
                Instant.parse("2026-09-28T19:00:00Z")
        );

        when(service.getAnalysisById(1L))
                .thenReturn(analysis);

        when(mapper.toResponse(analysis))
                .thenReturn(response);

        ResponseEntity<MedicationAnalysisResponse> result =
                controller.getAnalysisById(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1L, result.getBody().getId());
        assertEquals(
                "Demo Medicine",
                result.getBody().getMedicationName()
        );
        assertEquals(
                "500 mg",
                result.getBody().getStrength()
        );
        assertFalse(
                result.getBody().isAmbiguityDetected()
        );
        assertEquals(
                0.95,
                result.getBody().getConfidenceScore()
        );
    }

    @Test
    void shouldCreateMedicationAnalysis() {

        CreateMedicationAnalysisRequest request =
                new CreateMedicationAnalysisRequest();

        request.setOriginalText(
                "Take one capsule once daily at night"
        );
        request.setMedicationName("Test Capsule");
        request.setStrength("250 mg");
        request.setDosage("1 capsule");
        request.setFrequency("Once daily");
        request.setRoute("Oral");
        request.setAmbiguityDetected(false);
        request.setConfidenceScore(0.91);

        MedicationAnalysis savedAnalysis =
                new MedicationAnalysis();

        savedAnalysis.setOriginalText(
                request.getOriginalText()
        );
        savedAnalysis.setMedicationName(
                request.getMedicationName()
        );
        savedAnalysis.setStrength(request.getStrength());
        savedAnalysis.setDosage(request.getDosage());
        savedAnalysis.setFrequency(request.getFrequency());
        savedAnalysis.setRoute(request.getRoute());
        savedAnalysis.setAmbiguityDetected(
                request.isAmbiguityDetected()
        );
        savedAnalysis.setConfidenceScore(
                request.getConfidenceScore()
        );

        MedicationAnalysisResponse response =
                new MedicationAnalysisResponse();

        response.setId(2L);
        response.setOriginalText(request.getOriginalText());
        response.setMedicationName(request.getMedicationName());
        response.setStrength(request.getStrength());
        response.setDosage(request.getDosage());
        response.setFrequency(request.getFrequency());
        response.setRoute(request.getRoute());
        response.setAmbiguityDetected(
                request.isAmbiguityDetected()
        );
        response.setConfidenceScore(
                request.getConfidenceScore()
        );
        response.setCreatedAt(savedAnalysis.getCreatedAt());

        when(service.createAnalysis(any(MedicationAnalysis.class)))
                .thenReturn(savedAnalysis);

        when(mapper.toResponse(savedAnalysis))
                .thenReturn(response);

        ResponseEntity<MedicationAnalysisResponse> result =
                controller.createAnalysis(request);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(2L, result.getBody().getId());
        assertEquals(
                "Test Capsule",
                result.getBody().getMedicationName()
        );
        assertEquals(
                "250 mg",
                result.getBody().getStrength()
        );
        assertEquals(
                "1 capsule",
                result.getBody().getDosage()
        );
        assertEquals(
                "Once daily",
                result.getBody().getFrequency()
        );
        assertFalse(
                result.getBody().isAmbiguityDetected()
        );
        assertEquals(
                0.91,
                result.getBody().getConfidenceScore()
        );
    }

    @Test
    void shouldReturnAllAnalyses() {

        MedicationAnalysis firstAnalysis =
                new MedicationAnalysis();

        firstAnalysis.setMedicationName("Medicine One");

        MedicationAnalysis secondAnalysis =
                new MedicationAnalysis();

        secondAnalysis.setMedicationName("Medicine Two");

        MedicationAnalysisResponse firstResponse =
                new MedicationAnalysisResponse();

        firstResponse.setId(1L);
        firstResponse.setMedicationName("Medicine One");

        MedicationAnalysisResponse secondResponse =
                new MedicationAnalysisResponse();

        secondResponse.setId(2L);
        secondResponse.setMedicationName("Medicine Two");

        when(service.getAllAnalyses())
                .thenReturn(List.of(
                        firstAnalysis,
                        secondAnalysis
                ));

        when(mapper.toResponse(firstAnalysis))
                .thenReturn(firstResponse);

        when(mapper.toResponse(secondAnalysis))
                .thenReturn(secondResponse);

        ResponseEntity<List<MedicationAnalysisResponse>> result =
                controller.getAllAnalyses();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());

        assertEquals(
                "Medicine One",
                result.getBody().get(0).getMedicationName()
        );

        assertEquals(
                "Medicine Two",
                result.getBody().get(1).getMedicationName()
        );
    }

}
