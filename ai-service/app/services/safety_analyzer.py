import re


class SafetyAnalyzer:

    AMBIGUOUS_PATTERNS = {
        r"\bas directed\b":
            "Instruction 'as directed' requires additional context.",

        r"\bas needed\b":
            "The instruction does not define when the medicine is needed.",

        r"\bwhen required\b":
            "The instruction does not define a clear administration schedule.",

        r"\btake regularly\b":
            "The word 'regularly' does not specify an exact frequency.",

        r"\buse regularly\b":
            "The word 'regularly' does not specify an exact frequency.",

        r"\btake some\b":
            "The dosage amount is unclear.",

        r"\buse some\b":
            "The dosage amount is unclear.",
    }

    def detect_warnings(
        self,
        text: str,
    ) -> list[str]:

        warnings: list[str] = []

        for pattern, message in self.AMBIGUOUS_PATTERNS.items():
            if re.search(
                pattern,
                text,
                re.IGNORECASE,
            ):
                warnings.append(message)

        return warnings
