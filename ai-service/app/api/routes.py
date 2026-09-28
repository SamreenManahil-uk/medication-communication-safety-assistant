from fastapi import (
    APIRouter,
    File,
    HTTPException,
    UploadFile,
)

from app.schemas.analysis import (
    HybridSafetyResponse,
    ImageAnalysisResponse,
    TextAnalysisRequest,
    TextAnalysisResponse,
)
from app.services.medication_analyzer import MedicationAnalyzer
from app.services.ocr_service import OCRService


router = APIRouter()

analyzer = MedicationAnalyzer()
ocr_service = OCRService()


@router.get("/health")
def health() -> dict[str, str]:
    return {
        "status": "healthy",
        "service": "medication-ai-service",
    }


@router.post(
    "/analyze-text",
    response_model=TextAnalysisResponse,
)
def analyze_text(
    request: TextAnalysisRequest,
) -> TextAnalysisResponse:

    return analyzer.analyse(request.text)


@router.post(
    "/analyze-image",
    response_model=ImageAnalysisResponse,
)
async def analyze_image(
    image: UploadFile = File(...),
) -> ImageAnalysisResponse:

    try:
        extracted_text = await ocr_service.extract_text(
            image
        )

    except ValueError as exception:
        raise HTTPException(
            status_code=400,
            detail=str(exception),
        ) from exception

    analysis = analyzer.analyse(
        extracted_text
    )

    return ImageAnalysisResponse(
        extracted_text=extracted_text,
        analysis=analysis,
    )


@router.post(
    "/hybrid-analyze",
    response_model=HybridSafetyResponse,
)
def hybrid_analyze(
    request: TextAnalysisRequest,
) -> HybridSafetyResponse:

    from app.services.hybrid_safety_service import (
        HybridSafetyService,
    )

    service = HybridSafetyService()

    return service.analyse(
        request.text
    )
