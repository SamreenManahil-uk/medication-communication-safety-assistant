import cv2
import numpy as np
import pytesseract

from fastapi import UploadFile


class OCRService:

    ALLOWED_CONTENT_TYPES = {
        "image/jpeg",
        "image/png",
        "image/webp",
    }

    MAX_FILE_SIZE = 10 * 1024 * 1024

    async def extract_text(
        self,
        image: UploadFile,
    ) -> str:

        if image.content_type not in self.ALLOWED_CONTENT_TYPES:
            raise ValueError(
                "Unsupported image type. "
                "Use JPEG, PNG or WEBP."
            )

        image_bytes = await image.read()

        if not image_bytes:
            raise ValueError("Uploaded image is empty.")

        if len(image_bytes) > self.MAX_FILE_SIZE:
            raise ValueError(
                "Image exceeds the 10 MB size limit."
            )

        image_array = np.frombuffer(
            image_bytes,
            dtype=np.uint8,
        )

        decoded_image = cv2.imdecode(
            image_array,
            cv2.IMREAD_COLOR,
        )

        if decoded_image is None:
            raise ValueError(
                "Unable to decode the uploaded image."
            )

        processed_image = self._preprocess(
            decoded_image
        )

        text = pytesseract.image_to_string(
            processed_image,
            config="--oem 3 --psm 6",
        )

        cleaned_text = " ".join(
            text.strip().split()
        )

        if not cleaned_text:
            raise ValueError(
                "No readable text was detected in the image."
            )

        return cleaned_text

    def _preprocess(
        self,
        image: np.ndarray,
    ) -> np.ndarray:

        gray = cv2.cvtColor(
            image,
            cv2.COLOR_BGR2GRAY,
        )

        gray = cv2.GaussianBlur(
            gray,
            (3, 3),
            0,
        )

        processed = cv2.threshold(
            gray,
            0,
            255,
            cv2.THRESH_BINARY
            + cv2.THRESH_OTSU,
        )[1]

        return processed
