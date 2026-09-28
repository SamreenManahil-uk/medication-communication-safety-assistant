from unittest.mock import patch

from app.services.nlp_service import MedicationNLPService


def test_nlp_classifies_clear_instruction():

    fake_result = {
        "labels": [
            "clear medication instruction",
            "ambiguous medication instruction",
            "incomplete medication instruction",
        ],
        "scores": [
            0.91,
            0.06,
            0.03,
        ],
    }

    service = MedicationNLPService()

    with patch.object(
        service,
        "_classifier",
        return_value=lambda *args, **kwargs: fake_result,
    ):
        result = service.classify_instruction(
            "Paracetamol 500 mg take one tablet twice daily"
        )

    assert (
        result["classification"]
        == "clear medication instruction"
    )

    assert result["confidence"] == 0.91

    assert (
        result["scores"]["clear medication instruction"]
        == 0.91
    )


def test_nlp_classifies_ambiguous_instruction():

    fake_result = {
        "labels": [
            "ambiguous medication instruction",
            "incomplete medication instruction",
            "clear medication instruction",
        ],
        "scores": [
            0.84,
            0.11,
            0.05,
        ],
    }

    service = MedicationNLPService()

    with patch.object(
        service,
        "_classifier",
        return_value=lambda *args, **kwargs: fake_result,
    ):
        result = service.classify_instruction(
            "Take medication as directed"
        )

    assert (
        result["classification"]
        == "ambiguous medication instruction"
    )

    assert result["confidence"] == 0.84
