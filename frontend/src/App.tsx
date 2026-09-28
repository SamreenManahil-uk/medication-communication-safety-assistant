import { useState } from "react";
import { login, register } from "./api/authApi";
import { analyseMedication } from "./api/medicationApi";
import "./App.css";

interface AnalysisResult {
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
}

function App() {
  const [mode, setMode] = useState<"login" | "register">("login");

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const [instruction, setInstruction] = useState("");
  const [analysis, setAnalysis] = useState<AnalysisResult | null>(null);
  const [analysisLoading, setAnalysisLoading] = useState(false);
  const [analysisError, setAnalysisError] = useState("");

  const [loggedIn, setLoggedIn] = useState(
    Boolean(localStorage.getItem("medsafe_token"))
  );

  const storedUser = localStorage.getItem("medsafe_user");
  const user = storedUser ? JSON.parse(storedUser) : null;

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();

    setMessage("");
    setLoading(true);

    try {
      const response =
        mode === "login"
          ? await login({ email, password })
          : await register({ name, email, password });

      localStorage.setItem("medsafe_token", response.token);

      localStorage.setItem(
        "medsafe_user",
        JSON.stringify({
          userId: response.userId,
          name: response.name,
          email: response.email,
          role: response.role,
        })
      );

      setLoggedIn(true);
      setMessage("");
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : "Something went wrong."
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleAnalysis(event: React.FormEvent) {
    event.preventDefault();

    if (!instruction.trim()) {
      setAnalysisError("Please enter a medication instruction.");
      return;
    }

    const token = localStorage.getItem("medsafe_token");

    if (!token) {
      setAnalysisError("Your session has expired. Please sign in again.");
      setLoggedIn(false);
      return;
    }

    setAnalysisLoading(true);
    setAnalysisError("");
    setAnalysis(null);

    try {
      const response = await analyseMedication(
        {
          originalText: instruction.trim(),
        },
        token
      );

      setAnalysis(response);
    } catch (error) {
      setAnalysisError(
        error instanceof Error
          ? error.message
          : "Unable to analyse the instruction."
      );
    } finally {
      setAnalysisLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem("medsafe_token");
    localStorage.removeItem("medsafe_user");

    setLoggedIn(false);
    setInstruction("");
    setAnalysis(null);
  }

  if (loggedIn) {
    return (
      <div className="app-shell dashboard-shell">

        <header className="topbar">
          <div className="brand">
            <div className="brand-mark">M</div>

            <div>
              <strong>MedSafe</strong>
              <span>
                Medication Communication Safety Assistant
              </span>
            </div>
          </div>

          <div className="dashboard-user">
            <div>
              <strong>{user?.name || "User"}</strong>
              <span>{user?.role || "USER"}</span>
            </div>

            <button
              className="logout-button"
              onClick={logout}
            >
              Sign out
            </button>
          </div>
        </header>

        <main className="dashboard-main">

          <section className="dashboard-hero">
            <div>
              <p className="eyebrow">
                MEDICATION SAFETY WORKSPACE
              </p>

              <h1>
                Welcome back,{" "}
                <span>{user?.name || "there"}</span>.
              </h1>

              <p>
                Review medication instructions using MedSafe's
                hybrid rule-based and NLP safety analysis.
              </p>
            </div>

            <div className="status-card">
              <span className="status-dot">●</span>

              <div>
                <strong>AI Service Ready</strong>
                <small>Hybrid analysis available</small>
              </div>
            </div>
          </section>

          <section className="analysis-layout">

            <div className="analysis-card">

              <div className="card-heading">

                <div className="card-icon">✚</div>

                <div>
                  <p className="eyebrow">
                    AI-POWERED SAFETY CHECK
                  </p>

                  <h2>
                    Analyse medication instruction
                  </h2>

                  <p>
                    Enter the instruction exactly as it appears
                    on the medication label or prescription.
                  </p>
                </div>

              </div>

              <form onSubmit={handleAnalysis}>

                <label className="analysis-label">
                  <span>Medication instruction</span>

                  <textarea
                    value={instruction}
                    onChange={(e) =>
                      setInstruction(e.target.value)
                    }
                    maxLength={5000}
                    rows={7}
                    placeholder="Example: Paracetamol 500 mg take one tablet twice daily"
                  />
                </label>

                <div className="analysis-actions">

                  <span>
                    {instruction.length}/5000 characters
                  </span>

                  <button
                    className="analyse-button"
                    type="submit"
                    disabled={analysisLoading}
                  >
                    {analysisLoading
                      ? "Analysing..."
                      : "Analyse Instruction →"}
                  </button>

                </div>

              </form>

              {analysisError && (
                <div className="error-message">
                  {analysisError}
                </div>
              )}

            </div>

            <aside className="info-card">

              <p className="eyebrow">
                HOW IT WORKS
              </p>

              <div className="info-item">
                <span>✓</span>

                <div>
                  <strong>Rule-based checks</strong>

                  <p>
                    Medication wording is checked against
                    structured safety rules.
                  </p>
                </div>
              </div>

              <div className="info-item">
                <span>AI</span>

                <div>
                  <strong>NLP classification</strong>

                  <p>
                    Language patterns are analysed to identify
                    potentially unclear instructions.
                  </p>
                </div>
              </div>

              <div className="info-item">
                <span>!</span>

                <div>
                  <strong>Safety review</strong>

                  <p>
                    Instructions requiring additional review
                    are highlighted.
                  </p>
                </div>
              </div>

            </aside>

          </section>

          {analysis && (
            <section className="result-card">

              <div className="result-header">

                <div>
                  <p className="eyebrow">
                    ANALYSIS RESULT
                  </p>

                  <h2>
                    Medication safety assessment
                  </h2>
                </div>

                <span
                  className={`safety-badge ${
                    analysis.ambiguityDetected
                      ? "review"
                      : "clear"
                  }`}
                >
                  {analysis.ambiguityDetected
                    ? "REVIEW"
                    : "CLEAR"}
                </span>

              </div>

              <div className="result-grid">

                <div>
                  <span>Medication</span>
                  <strong>
                    {analysis.medicationName || "Not identified"}
                  </strong>
                </div>

                <div>
                  <span>Used for</span>
                  <strong>
                    {analysis.usedFor || "Not available"}
                  </strong>
                </div>

                <div>
                  <span>Strength</span>
                  <strong>
                    {analysis.strength || "Not identified"}
                  </strong>
                </div>

                <div>
                  <span>Dosage</span>
                  <strong>
                    {analysis.dosage || "Not identified"}
                  </strong>
                </div>

                <div>
                  <span>Frequency</span>
                  <strong>
                    {analysis.frequency || "Not identified"}
                  </strong>
                </div>

                <div>
                  <span>Route</span>
                  <strong>
                    {analysis.route || "Not identified"}
                  </strong>
                </div>

                <div>
                  <span>Review required</span>
                  <strong>
                    {analysis.ambiguityDetected
                      ? "Yes"
                      : "No"}
                  </strong>
                </div>

              </div>

              <div className="confidence-row">

                <div>
                  <span>Confidence score</span>

                  <strong>
                    {analysis.confidenceScore != null
                      ? `${Math.round(
                          analysis.confidenceScore * 100
                        )}%`
                      : "—"}
                  </strong>
                </div>

              </div>

              <div className="medical-disclaimer">

                <strong>Important</strong>

                <p>
                  MedSafe supports medication communication
                  safety. It does not replace advice from a
                  qualified healthcare professional.
                </p>

              </div>

            </section>
          )}

        </main>

        <footer>
          <strong>MedSafe</strong>

          <span>
            AI-assisted medication safety analysis
          </span>
        </footer>

      </div>
    );
  }

  return (
    <div className="app-shell">

      <header className="topbar">

        <div className="brand">

          <div className="brand-mark">M</div>

          <div>
            <strong>MedSafe</strong>

            <span>
              Medication Communication Safety Assistant
            </span>
          </div>

        </div>

        <div className="secure-badge">
          <span>●</span> Secure AI-assisted analysis
        </div>

      </header>

      <main className="auth-page">

        <section className="medical-panel">

          <div className="medical-icon">✚</div>

          <p className="eyebrow">
            MEDICATION SAFETY PLATFORM
          </p>

          <h1>
            Communicate medication
            <br />
            instructions with confidence.
          </h1>

          <p className="hero-copy">
            A hybrid AI safety assistant combining rule-based
            analysis and NLP to identify potentially unclear
            medication instructions.
          </p>

          <div className="feature-list">

            <div>
              <span>✓</span>

              <div>
                <strong>Hybrid AI analysis</strong>
                <small>
                  Rules + NLP working together
                </small>
              </div>
            </div>

            <div>
              <span>✓</span>

              <div>
                <strong>Safety-focused</strong>
                <small>
                  Potential ambiguity highlighted for review
                </small>
              </div>
            </div>

            <div>
              <span>✓</span>

              <div>
                <strong>Medication communication</strong>
                <small>
                  Designed for clearer instructions
                </small>
              </div>
            </div>

          </div>

        </section>

        <section className="auth-card">

          <div className="auth-heading">

            <p className="eyebrow">
              {mode === "login"
                ? "WELCOME BACK"
                : "GET STARTED"}
            </p>

            <h2>
              {mode === "login"
                ? "Sign in to MedSafe"
                : "Create your account"}
            </h2>

            <p>
              {mode === "login"
                ? "Access your medication safety workspace."
                : "Create an account to use the safety analysis assistant."}
            </p>

          </div>

          <form onSubmit={handleSubmit}>

            {mode === "register" && (
              <label>

                <span>Full name</span>

                <input
                  value={name}
                  onChange={(e) =>
                    setName(e.target.value)
                  }
                  placeholder="Enter your full name"
                  maxLength={100}
                  required
                />

              </label>
            )}

            <label>

              <span>Email address</span>

              <input
                type="email"
                value={email}
                onChange={(e) =>
                  setEmail(e.target.value)
                }
                placeholder="you@example.com"
                required
              />

            </label>

            <label>

              <span>Password</span>

              <input
                type="password"
                value={password}
                onChange={(e) =>
                  setPassword(e.target.value)
                }
                placeholder="Enter your password"
                minLength={8}
                maxLength={100}
                required
              />

            </label>

            <button
              className="auth-submit"
              type="submit"
              disabled={loading}
            >
              {loading
                ? "Please wait..."
                : mode === "login"
                  ? "Sign in"
                  : "Create account"}
            </button>

          </form>

          {message && (
            <div className="auth-message">
              {message}
            </div>
          )}

          <div className="auth-switch">

            <span>
              {mode === "login"
                ? "Don't have an account?"
                : "Already have an account?"}
            </span>

            <button
              type="button"
              onClick={() =>
                setMode(
                  mode === "login"
                    ? "register"
                    : "login"
                )
              }
            >
              {mode === "login"
                ? "Create account"
                : "Sign in"}
            </button>

          </div>

          <div className="medical-disclaimer">

            <strong>Important</strong>

            <p>
              MedSafe supports medication communication safety.
              It does not replace advice from a qualified
              healthcare professional.
            </p>

          </div>

        </section>

      </main>

      <footer>
        <strong>MedSafe</strong>

        <span>
          AI-assisted medication safety analysis
        </span>
      </footer>

    </div>
  );
}

export default App;
