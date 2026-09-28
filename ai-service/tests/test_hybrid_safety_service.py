from unittest.mock import Mock

from app.services.hybrid_safety_service import (
    HybridSafetyService,
)
from app.services.medication_analyzer import (
    MedicationAnalyzer,
)


def create_nlp_mock(
    classification: str,
    confidence: float,
):

    mock = Mock()

    mock.classify_instruction.return_value = {
        "classification": classification,
        "confidence": confidence,
        "scores": {},
    }

    return mock


def test_clear_instruction_is_clear():

    nlp = create_nlp_mock(
        "clear medication instruction",
        0.94,
    )

    service = HybridSafetyService(
        MedicationAnalyzer(),
        nlp,
    )

    result = service.analyse(
        "Paracetamol 500 mg "
        "take one tablet twice daily"
    )

    assert result.medication_name == "Paracetamol"
    assert result.rule_confidence == 1.0

    assert (
        result.nlp_classification
        == "clear medication instruction"
    )

    assert result.safety_level == "CLEAR"
    assert result.needs_review is False


def test_ambiguous_instruction_requires_review():

    nlp = create_nlp_mock(
        "ambiguous medication instruction",
        0.89,
    )

    service = HybridSafetyService(
        MedicationAnalyzer(),
        nlp,
    )

    result = service.analyse(
        "Paracetamol 500 mg take as directed"
    )

    assert result.needs_review is True
    assert result.safety_level == "HIGH_REVIEW"

    assert result.rule_confidence == 0.4
    assert len(result.warnings) > 0


def test_incomplete_instruction_is_high_review():

    nlp = create_nlp_mock(
        "incomplete medication instruction",
        0.91,
    )

    service = HybridSafetyService(
        MedicationAnalyzer(),
        nlp,
    )

    result = service.analyse(
        "Paracetamol"
    )

    assert result.needs_review is True
    assert result.safety_level == "HIGH_REVIEW"

    assert result.rule_confidence < 0.6


def test_rules_can_force_review_even_when_nlp_says_clear():

    nlp = create_nlp_mock(
        "clear medication instruction",
        0.90,
    )

    service = HybridSafetyService(
        MedicationAnalyzer(),
        nlp,
    )

    result = service.analyse(
        "Ibuprofen 200 mg take one tablet"
    )

    assert result.frequency is None
    assert result.needs_review is True
    assert result.safety_level == "REVIEW"
