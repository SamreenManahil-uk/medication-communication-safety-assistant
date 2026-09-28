from functools import lru_cache

from transformers import pipeline


class MedicationNLPService:

    MODEL_NAME = "facebook/bart-large-mnli"

    LABELS = [
        "clear medication instruction",
        "ambiguous medication instruction",
        "incomplete medication instruction",
    ]

    @staticmethod
    @lru_cache(maxsize=1)
    def _classifier():
        return pipeline(
            task="zero-shot-classification",
            model=MedicationNLPService.MODEL_NAME,
        )

    def classify_instruction(
        self,
        text: str,
    ) -> dict:

        classifier = self._classifier()

        result = classifier(
            text,
            candidate_labels=self.LABELS,
            hypothesis_template=(
                "This medication instruction is {}."
            ),
        )

        scores = {
            label: round(float(score), 4)
            for label, score in zip(
                result["labels"],
                result["scores"],
            )
        }

        return {
            "classification": result["labels"][0],
            "confidence": round(
                float(result["scores"][0]),
                4,
            ),
            "scores": scores,
        }
