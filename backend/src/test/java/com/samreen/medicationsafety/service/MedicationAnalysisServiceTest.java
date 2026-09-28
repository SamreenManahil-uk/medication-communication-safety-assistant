package com.samreen.medicationsafety.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.samreen.medicationsafety.entity.MedicationAnalysis;
import com.samreen.medicationsafety.exception.ResourceNotFoundException;
import com.samreen.medicationsafety.repository.MedicationAnalysisRepository;

@ExtendWith(MockitoExtension.class)
class MedicationAnalysisServiceTest {

    @Mock
    private MedicationAnalysisRepository repository;

    private MedicationAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new MedicationAnalysisService(repository);
    }

    @Test
    void shouldCreateAnalysis() {

        MedicationAnalysis analysis = new MedicationAnalysis();

        analysis.setOriginalText(
                "Take one tablet twice daily"
        );
        analysis.setMedicationName("Demo Medicine");

        when(repository.save(analysis))
                .thenReturn(analysis);

        MedicationAnalysis result =
                service.createAnalysis(analysis);

        assertSame(analysis, result);

        verify(repository).save(analysis);
    }

    @Test
    void shouldReturnAllAnalyses() {

        MedicationAnalysis first =
                new MedicationAnalysis();

        first.setMedicationName("Medicine One");

        MedicationAnalysis second =
                new MedicationAnalysis();

        second.setMedicationName("Medicine Two");

        when(repository.findAll())
                .thenReturn(List.of(first, second));

        List<MedicationAnalysis> result =
                service.getAllAnalyses();

        assertEquals(2, result.size());
        assertEquals(
                "Medicine One",
                result.get(0).getMedicationName()
        );
        assertEquals(
                "Medicine Two",
                result.get(1).getMedicationName()
        );

        verify(repository).findAll();
    }

    @Test
    void shouldReturnAnalysisById() {

        MedicationAnalysis analysis =
                new MedicationAnalysis();

        analysis.setMedicationName("Demo Medicine");

        when(repository.findById(1L))
                .thenReturn(Optional.of(analysis));

        MedicationAnalysis result =
                service.getAnalysisById(1L);

        assertSame(analysis, result);
        assertEquals(
                "Demo Medicine",
                result.getMedicationName()
        );

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAnalysisDoesNotExist() {

        when(repository.findById(99999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.getAnalysisById(99999L)
                );

        assertEquals(
                "Medication analysis not found with id: 99999",
                exception.getMessage()
        );

        verify(repository).findById(99999L);
    }
}
