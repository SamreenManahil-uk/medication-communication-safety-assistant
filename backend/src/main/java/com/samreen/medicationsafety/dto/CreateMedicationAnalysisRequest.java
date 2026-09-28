package com.samreen.medicationsafety.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateMedicationAnalysisRequest {

    @NotBlank(message = "Original text is required")
    @Size(max = 5000, message = "Original text must not exceed 5000 characters")
    private String originalText;

    @Size(max = 255)
    private String medicationName;

    @Size(max = 255)
    private String strength;

    @Size(max = 255)
    private String dosage;

    @Size(max = 255)
    private String frequency;

    @Size(max = 255)
    private String route;

    private boolean ambiguityDetected;

    @DecimalMin(value = "0.0", message = "Confidence score must be at least 0")
    @DecimalMax(value = "1.0", message = "Confidence score must not exceed 1")
    private Double confidenceScore;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public boolean isAmbiguityDetected() {
        return ambiguityDetected;
    }

    public void setAmbiguityDetected(boolean ambiguityDetected) {
        this.ambiguityDetected = ambiguityDetected;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
