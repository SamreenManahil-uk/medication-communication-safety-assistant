package com.samreen.medicationsafety.ai.dto;

public class AiTextAnalysisRequest {

    private String text;

    public AiTextAnalysisRequest() {
    }

    public AiTextAnalysisRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
