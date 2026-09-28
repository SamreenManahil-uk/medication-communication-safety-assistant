from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_health_endpoint():
    response = client.get("/api/v1/health")

    assert response.status_code == 200
    assert response.json()["status"] == "healthy"


def test_analyze_clear_medication_instruction():
    response = client.post(
        "/api/v1/analyze-text",
        json={
            "text": (
                "Paracetamol 500 mg take one tablet "
                "twice daily after food"
            )
        },
    )

    assert response.status_code == 200

    data = response.json()

    assert data["strength"] == "500 mg"
    assert data["dosage"] == "one tablet"
    assert data["frequency"] == "Twice daily"
    assert data["route"] == "Oral"
    assert data["ambiguity_detected"] is False
    assert data["confidence_score"] == 1.0


def test_analyze_ambiguous_instruction():
    response = client.post(
        "/api/v1/analyze-text",
        json={
            "text": "Take medicine as directed"
        },
    )

    assert response.status_code == 200

    data = response.json()

    assert data["ambiguity_detected"] is True
    assert data["confidence_score"] < 1.0
    assert len(data["warnings"]) > 0


def test_empty_text_should_fail_validation():
    response = client.post(
        "/api/v1/analyze-text",
        json={"text": ""},
    )

    assert response.status_code == 422
