import re

from app.core.medication_knowledge import KNOWN_MEDICATIONS
from app.schemas.analysis import TextAnalysisResponse
from app.services.safety_analyzer import SafetyAnalyzer


class MedicationAnalyzer:

    FREQUENCY_PATTERNS = {
        r"\bonce daily\b": "Once daily",
        r"\bonce a day\b": "Once daily",
        r"\btwice daily\b": "Twice daily",
        r"\btwice a day\b": "Twice daily",
        r"\bthree times daily\b": "Three times daily",
        r"\bthree times a day\b": "Three times daily",
        r"\bfour times daily\b": "Four times daily",
        r"\bfour times a day\b": "Four times daily",
        r"\bevery\s+(\d+)\s+hours?\b": None,
        r"\bat night\b": "At night",
        r"\bin the morning\b": "In the morning",
    }

    ROUTE_PATTERNS = {
        r"\bby mouth\b": "Oral",
        r"\borally\b": "Oral",
        r"\btablet\b": "Oral",
        r"\btablets\b": "Oral",
        r"\bcapsule\b": "Oral",
        r"\bcapsules\b": "Oral",
        r"\btopically\b": "Topical",
        r"\bon the skin\b": "Topical",
        r"\binhale\b": "Inhalation",
        r"\binhalation\b": "Inhalation",
        r"\bpuff\b": "Inhalation",
        r"\bpuffs\b": "Inhalation",
    }

    def __init__(self):
        self.safety_analyzer = SafetyAnalyzer()

    def analyse(
        self,
        text: str,
    ) -> TextAnalysisResponse:

        cleaned_text = " ".join(
            text.strip().split()
        )

        medication_name = self._extract_medication_name(
            cleaned_text
        )

        strength = self._extract_strength(
            cleaned_text
        )

        used_for = None
        if medication_name:
            medication_details = KNOWN_MEDICATIONS.get(
                medication_name.lower()
            )
            if medication_details:
                used_for = medication_details.get("used_for")

        dosage = self._extract_dosage(
            cleaned_text
        )

        frequency = self._extract_frequency(
            cleaned_text
        )

        route = self._extract_route(
            cleaned_text
        )

        warnings = self.safety_analyzer.detect_warnings(
            cleaned_text
        )

        if medication_name is None:
            warnings.append(
                "Medication name could not be confidently identified."
            )

        if dosage is None:
            warnings.append(
                "Dosage could not be confidently identified."
            )

        if frequency is None:
            warnings.append(
                "Frequency could not be confidently identified."
            )

        if route is None:
            warnings.append(
                "Administration route could not be confidently identified."
            )

        ambiguity_detected = len(warnings) > 0

        detected_fields = sum(
            value is not None
            for value in [
                medication_name,
                strength,
                dosage,
                frequency,
                route,
            ]
        )

        confidence_score = round(
            detected_fields / 5,
            2,
        )

        return TextAnalysisResponse(
            original_text=cleaned_text,
            medication_name=medication_name,
            used_for=used_for,
            strength=strength,
            dosage=dosage,
            frequency=frequency,
            route=route,
            ambiguity_detected=ambiguity_detected,
            confidence_score=confidence_score,
            warnings=warnings,
        )

    def _extract_medication_name(
        self,
        text: str,
    ) -> str | None:

        lowered = text.lower()

        for medication, details in KNOWN_MEDICATIONS.items():

            names = [
                medication,
                *details["aliases"],
            ]

            for name in names:

                if re.search(
                    rf"\b{re.escape(name)}\b",
                    lowered,
                ):
                    return medication.title()

        return None

    def _extract_strength(
        self,
        text: str,
    ) -> str | None:

        match = re.search(
            r"\b(\d+(?:\.\d+)?)\s*"
            r"(mg|mcg|g|ml)\b",
            text,
            re.IGNORECASE,
        )

        if not match:
            return None

        return (
            f"{match.group(1)} "
            f"{match.group(2).lower()}"
        )

    def _extract_dosage(
        self,
        text: str,
    ) -> str | None:

        match = re.search(
            r"\b(one|two|three|four|\d+)\s+"
            r"(tablet|tablets|capsule|capsules|"
            r"puff|puffs|dose|doses)\b",
            text,
            re.IGNORECASE,
        )

        if not match:
            return None

        return (
            f"{match.group(1)} "
            f"{match.group(2)}"
        ).lower()

    def _extract_frequency(
        self,
        text: str,
    ) -> str | None:

        for pattern, value in self.FREQUENCY_PATTERNS.items():

            match = re.search(
                pattern,
                text,
                re.IGNORECASE,
            )

            if not match:
                continue

            if value is not None:
                return value

            if match.groups():
                return (
                    f"Every {match.group(1)} hours"
                )

        return None

    def _extract_route(
        self,
        text: str,
    ) -> str | None:

        for pattern, route in self.ROUTE_PATTERNS.items():

            if re.search(
                pattern,
                text,
                re.IGNORECASE,
            ):
                return route

        return None
