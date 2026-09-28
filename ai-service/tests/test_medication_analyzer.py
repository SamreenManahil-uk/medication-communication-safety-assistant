from app.services.medication_analyzer import MedicationAnalyzer


analyzer = MedicationAnalyzer()


def test_complete_paracetamol_instruction():

    result = analyzer.analyse(
        "Paracetamol 500 mg take one tablet twice daily"
    )

    assert result.medication_name == "Paracetamol"
    assert result.strength == "500 mg"
    assert result.dosage == "one tablet"
    assert result.frequency == "Twice daily"
    assert result.route == "Oral"

    assert result.ambiguity_detected is False
    assert result.confidence_score == 1.0


def test_medication_alias_detection():

    result = analyzer.analyse(
        "Acetaminophen 500 mg take one tablet twice daily"
    )

    assert result.medication_name == "Paracetamol"


def test_ambiguous_as_directed_instruction():

    result = analyzer.analyse(
        "Paracetamol 500 mg take as directed"
    )

    assert result.ambiguity_detected is True

    assert any(
        "as directed" in warning.lower()
        for warning in result.warnings
    )


def test_missing_frequency_reduces_confidence():

    result = analyzer.analyse(
        "Ibuprofen 200 mg take one tablet"
    )

    assert result.medication_name == "Ibuprofen"
    assert result.frequency is None
    assert result.ambiguity_detected is True
    assert result.confidence_score < 1.0


def test_inhaler_route_detection():

    result = analyzer.analyse(
        "Salbutamol 100 mcg inhale two puffs twice daily"
    )

    assert result.medication_name == "Salbutamol"
    assert result.strength == "100 mcg"
    assert result.dosage == "two puffs"
    assert result.frequency == "Twice daily"
    assert result.route == "Inhalation"
