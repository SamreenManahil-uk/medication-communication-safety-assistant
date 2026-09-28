import cv2
import numpy as np
import pytest

from fastapi import UploadFile
from io import BytesIO

from app.services.ocr_service import OCRService


@pytest.mark.asyncio
async def test_ocr_extracts_medication_text():

    image = np.full(
        (250, 1000, 3),
        255,
        dtype=np.uint8,
    )

    cv2.putText(
        image,
        "Paracetamol 500 mg",
        (30, 100),
        cv2.FONT_HERSHEY_SIMPLEX,
        1.5,
        (0, 0, 0),
        3,
        cv2.LINE_AA,
    )

    cv2.putText(
        image,
        "Take one tablet twice daily",
        (30, 180),
        cv2.FONT_HERSHEY_SIMPLEX,
        1.0,
        (0, 0, 0),
        2,
        cv2.LINE_AA,
    )

    success, encoded = cv2.imencode(
        ".png",
        image,
    )

    assert success

    upload = UploadFile(
        filename="medicine.png",
        file=BytesIO(encoded.tobytes()),
        headers={
            "content-type": "image/png"
        },
    )

    service = OCRService()

    text = await service.extract_text(upload)

    assert "500" in text
    assert "tablet" in text.lower()


@pytest.mark.asyncio
async def test_ocr_rejects_unsupported_file():

    upload = UploadFile(
        filename="medicine.txt",
        file=BytesIO(b"not an image"),
        headers={
            "content-type": "text/plain"
        },
    )

    service = OCRService()

    with pytest.raises(
        ValueError,
        match="Unsupported image type",
    ):
        await service.extract_text(upload)
