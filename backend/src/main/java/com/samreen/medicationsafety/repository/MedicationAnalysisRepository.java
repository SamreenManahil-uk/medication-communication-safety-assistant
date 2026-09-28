package com.samreen.medicationsafety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samreen.medicationsafety.entity.MedicationAnalysis;

@Repository
public interface MedicationAnalysisRepository
        extends JpaRepository<MedicationAnalysis, Long> {
}
