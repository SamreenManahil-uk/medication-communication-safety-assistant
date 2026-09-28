from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.routes import router


app = FastAPI(
    title="Medication Communication Safety AI Service",
    description=(
        "AI service for medication instruction extraction, "
        "ambiguity detection, OCR and NLP analysis."
    ),
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://127.0.0.1:5174",
        "http://localhost:5174",
        "http://127.0.0.1:5173",
        "http://localhost:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


app.include_router(
    router,
    prefix="/api/v1",
)


@app.get("/")
def root() -> dict[str, str]:
    return {
        "service": "Medication Communication Safety AI Service",
        "status": "running",
    }
