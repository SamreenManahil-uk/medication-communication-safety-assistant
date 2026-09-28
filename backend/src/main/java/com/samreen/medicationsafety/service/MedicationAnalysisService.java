package com.samreen.medicationsafety.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samreen.medicationsafety.entity.MedicationAnalysis;
import com.samreen.medicationsafety.exception.ResourceNotFoundException;
import com.samreen.medicationsafety.repository.MedicationAnalysisRepository;

@Service
public class MedicationAnalysisService {

    private final MedicationAnalysisRepository repository;

    public MedicationAnalysisService(
            MedicationAnalysisRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MedicationAnalysis createAnalysis(
            MedicationAnalysis analysis) {
        return repository.save(analysis);
    }

    @Transactional(readOnly = true)
    public List<MedicationAnalysis> getAllAnalyses() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public MedicationAnalysis getAnalysisById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medication analysis not found with id: " + id
                        )
                );
    }
}
