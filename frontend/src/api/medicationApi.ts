export interface MedicationAnalysisRequest {
  originalText: string;
}

export interface MedicationAnalysisResponse {
  id?: number;
  originalText: string;
  medicationName?: string;
  usedFor?: string;
  strength?: string;
  dosage?: string;
  frequency?: string;
  route?: string;
  ambiguityDetected?: boolean;
  confidenceScore?: number;
  safetyLevel?: string;
  needsReview?: boolean;
  warnings?: string[];
}

interface AiResponse {
  used_for?: string;
  original_text: string;
  medication_name?: string;
  strength?: string;
  dosage?: string;
  frequency?: string;
  route?: string;
  rule_confidence?: number;
  nlp_confidence?: number;
  safety_level?: string;
  needs_review?: boolean;
  warnings?: string[];
}

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080";

const AI_BASE_URL =
  import.meta.env.VITE_AI_BASE_URL || "http://127.0.0.1:8000";

export async function analyseMedication(
  request: MedicationAnalysisRequest,
  token: string
): Promise<MedicationAnalysisResponse> {

  // 1. Ask the AI service to analyse the medication instruction
  const aiResponse = await fetch(
    `${AI_BASE_URL}/api/v1/hybrid-analyze`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        text: request.originalText,
      }),
    }
  );

  if (!aiResponse.ok) {
    const message = await aiResponse.text();
    throw new Error(
      `AI analysis failed (${aiResponse.status}): ${message}`
    );
  }

  const ai: AiResponse = await aiResponse.json();

  // 2. Save the AI result through the authenticated Spring API
  const backendResponse = await fetch(
    `${API_BASE_URL}/api/analyses`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        originalText: ai.original_text,
        medicationName: ai.medication_name || null,
        strength: ai.strength || null,
        dosage: ai.dosage || null,
        frequency: ai.frequency || null,
        route: ai.route || null,
        ambiguityDetected: ai.needs_review ?? false,
        confidenceScore: ai.rule_confidence ?? ai.nlp_confidence ?? null,
      }),
    }
  );

  if (!backendResponse.ok) {
    const message = await backendResponse.text();
    throw new Error(
      `Saving analysis failed (${backendResponse.status}): ${message}`
    );
  }

  const saved = await backendResponse.json();

  return {
    ...saved,
    usedFor: ai.used_for,
    safetyLevel: ai.safety_level,
    needsReview: ai.needs_review,
    warnings: ai.warnings || [],
  };
}
