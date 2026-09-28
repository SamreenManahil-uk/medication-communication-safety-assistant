package com.samreen.medicationsafety.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samreen.medicationsafety.dto.CreateMedicationAnalysisRequest;
import com.samreen.medicationsafety.dto.MedicationAnalysisResponse;
import com.samreen.medicationsafety.entity.MedicationAnalysis;
import com.samreen.medicationsafety.mapper.MedicationAnalysisMapper;
import com.samreen.medicationsafety.service.MedicationAnalysisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/analyses")
public class MedicationAnalysisController {

    private final MedicationAnalysisService service;
    private final MedicationAnalysisMapper mapper;

    public MedicationAnalysisController(
            MedicationAnalysisService service,
            MedicationAnalysisMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<MedicationAnalysisResponse> createAnalysis(
            @Valid @RequestBody CreateMedicationAnalysisRequest request) {

        MedicationAnalysis analysis = new MedicationAnalysis();

        analysis.setOriginalText(request.getOriginalText());
        analysis.setMedicationName(request.getMedicationName());
        analysis.setStrength(request.getStrength());
        analysis.setDosage(request.getDosage());
        analysis.setFrequency(request.getFrequency());
        analysis.setRoute(request.getRoute());
        analysis.setAmbiguityDetected(request.isAmbiguityDetected());
        analysis.setConfidenceScore(request.getConfidenceScore());

        MedicationAnalysis saved =
                service.createAnalysis(analysis);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<MedicationAnalysisResponse>> getAllAnalyses() {

        List<MedicationAnalysisResponse> responses =
                service.getAllAnalyses()
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationAnalysisResponse> getAnalysisById(
            @PathVariable Long id) {

        MedicationAnalysis analysis =
                service.getAnalysisById(id);

        return ResponseEntity.ok(
                mapper.toResponse(analysis)
        );
    }
}
