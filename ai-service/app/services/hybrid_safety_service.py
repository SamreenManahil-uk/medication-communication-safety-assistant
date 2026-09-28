from app.schemas.analysis import HybridSafetyResponse
from app.services.medication_analyzer import MedicationAnalyzer
from app.services.nlp_service import MedicationNLPService


class HybridSafetyService:

    def __init__(
        self,
        medication_analyzer=None,
        nlp_service=None,
    ):
        self.medication_analyzer = (
            medication_analyzer
            or MedicationAnalyzer()
        )

        self.nlp_service = (
            nlp_service
            or MedicationNLPService()
        )

    def analyse(self, text: str) -> HybridSafetyResponse:

        rule_result = (
            self.medication_analyzer.analyse(text)
        )

        nlp_result = (
            self.nlp_service.classify_instruction(text)
        )

        warnings = list(rule_result.warnings)

        nlp_classification = (
            nlp_result["classification"]
        )

        nlp_confidence = float(
            nlp_result["confidence"]
        )

        # ------------------------------------------------
        # SAFETY PRINCIPLE
        # ------------------------------------------------
        # Deterministic medication rules are authoritative.
        # Transformer NLP is supplementary only.
        #
        # A confident Transformer prediction must NOT
        # override missing dosage/frequency/route or
        # explicit ambiguity detected by the rules engine.
        # ------------------------------------------------

        rule_needs_review = (
            rule_result.ambiguity_detected
            or rule_result.confidence_score < 0.6
        )

        transformer_needs_review = (
            nlp_classification
            != "clear medication instruction"
            and nlp_confidence >= 0.70
        )

        needs_review = (
            rule_needs_review
            or transformer_needs_review
        )

        # Rule confidence determines the primary
        # safety severity.
        if rule_result.confidence_score < 0.6:
            safety_level = "HIGH_REVIEW"

        elif rule_needs_review:
            safety_level = "REVIEW"

        elif transformer_needs_review:
            safety_level = "REVIEW"

        else:
            safety_level = "CLEAR"

        # If NLP disagrees with the rule engine,
        # record it as an additional review signal.
        if transformer_needs_review:
            warnings.append(
                "NLP detected potentially ambiguous or "
                "incomplete medication wording."
            )

        warnings = list(dict.fromkeys(warnings))

        return HybridSafetyResponse(
            original_text=rule_result.original_text,

            medication_name=rule_result.medication_name,
            used_for=rule_result.used_for,
            strength=rule_result.strength,
            dosage=rule_result.dosage,
            frequency=rule_result.frequency,
            route=rule_result.route,

            rule_confidence=(
                rule_result.confidence_score
            ),

            nlp_classification=nlp_classification,
            nlp_confidence=nlp_confidence,

            safety_level=safety_level,
            needs_review=needs_review,

            warnings=warnings,
        )
