package com.samreen.medicationsafety.ai.dto;

import java.util.List;

public class AiAnalysisResponse {

    private String originalText;
    private String medicationName;
    private String strength;
    private String dosage;
    private String frequency;
    private String route;

    private double ruleConfidence;

    private String nlpClassification;
    private double nlpConfidence;

    private String safetyLevel;
    private boolean needsReview;

    private List<String> warnings;

    public AiAnalysisResponse() {
    }

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

    public double getRuleConfidence() {
        return ruleConfidence;
    }

    public void setRuleConfidence(double ruleConfidence) {
        this.ruleConfidence = ruleConfidence;
    }

    public String getNlpClassification() {
        return nlpClassification;
    }

    public void setNlpClassification(String nlpClassification) {
        this.nlpClassification = nlpClassification;
    }

    public double getNlpConfidence() {
        return nlpConfidence;
    }

    public void setNlpConfidence(double nlpConfidence) {
        this.nlpConfidence = nlpConfidence;
    }

    public String getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(String safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public boolean isNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
