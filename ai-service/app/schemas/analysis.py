from pydantic import BaseModel, Field


class TextAnalysisRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=1,
        max_length=5000,
        description="Medication instruction text to analyse",
    )


class TextAnalysisResponse(BaseModel):
    original_text: str
    medication_name: str | None = None
    used_for: str | None = None
    strength: str | None = None
    dosage: str | None = None
    frequency: str | None = None
    route: str | None = None
    ambiguity_detected: bool
    confidence_score: float = Field(
        ge=0.0,
        le=1.0,
    )
    warnings: list[str]


class ImageAnalysisResponse(BaseModel):
    extracted_text: str
    analysis: TextAnalysisResponse


class HybridSafetyResponse(BaseModel):
    original_text: str

    medication_name: str | None = None
    used_for: str | None = None
    strength: str | None = None
    dosage: str | None = None
    frequency: str | None = None
    route: str | None = None

    rule_confidence: float = Field(
        ge=0.0,
        le=1.0,
    )

    nlp_classification: str
    nlp_confidence: float = Field(
        ge=0.0,
        le=1.0,
    )

    safety_level: str
    needs_review: bool

    warnings: list[str]
