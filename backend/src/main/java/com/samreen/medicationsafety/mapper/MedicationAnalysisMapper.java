package com.samreen.medicationsafety.mapper;

import org.springframework.stereotype.Component;

import com.samreen.medicationsafety.dto.MedicationAnalysisResponse;
import com.samreen.medicationsafety.entity.MedicationAnalysis;

@Component
public class MedicationAnalysisMapper {

    public MedicationAnalysisResponse toResponse(
            MedicationAnalysis analysis) {

        MedicationAnalysisResponse response =
                new MedicationAnalysisResponse();

        response.setId(analysis.getId());
        response.setOriginalText(analysis.getOriginalText());
        response.setMedicationName(analysis.getMedicationName());
        response.setStrength(analysis.getStrength());
        response.setDosage(analysis.getDosage());
        response.setFrequency(analysis.getFrequency());
        response.setRoute(analysis.getRoute());
        response.setAmbiguityDetected(analysis.isAmbiguityDetected());
        response.setConfidenceScore(analysis.getConfidenceScore());
        response.setCreatedAt(analysis.getCreatedAt());

        return response;
    }
}
