package com.samreen.medicationsafety;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.samreen.medicationsafety.dto.MedicationAnalysisResponse;
import com.samreen.medicationsafety.entity.MedicationAnalysis;
import com.samreen.medicationsafety.mapper.MedicationAnalysisMapper;

class MedicationAnalysisMapperTest {

    private final MedicationAnalysisMapper mapper =
            new MedicationAnalysisMapper();

    @Test
    void shouldMapMedicationAnalysisToResponse() {

        MedicationAnalysis analysis = new MedicationAnalysis();

        analysis.setOriginalText(
                "Take one tablet twice daily after food"
        );
        analysis.setMedicationName("Demo Medicine");
        analysis.setStrength("500 mg");
        analysis.setDosage("1 tablet");
        analysis.setFrequency("Twice daily");
        analysis.setRoute("Oral");
        analysis.setAmbiguityDetected(false);
        analysis.setConfidenceScore(0.95);

        MedicationAnalysisResponse response =
                mapper.toResponse(analysis);

        assertNull(response.getId());

        assertEquals(
                "Take one tablet twice daily after food",
                response.getOriginalText()
        );

        assertEquals(
                "Demo Medicine",
                response.getMedicationName()
        );

        assertEquals("500 mg", response.getStrength());
        assertEquals("1 tablet", response.getDosage());
        assertEquals("Twice daily", response.getFrequency());
        assertEquals("Oral", response.getRoute());

        assertFalse(response.isAmbiguityDetected());

        assertEquals(
                0.95,
                response.getConfidenceScore()
        );

        assertNotNull(response.getCreatedAt());
        assertEquals(
                analysis.getCreatedAt(),
                response.getCreatedAt()
        );
    }
}
