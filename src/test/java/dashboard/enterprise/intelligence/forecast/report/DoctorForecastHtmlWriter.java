package dashboard.enterprise.intelligence.forecast.report;

import dashboard.enterprise.intelligence.forecast.model.ForecastSnapshot;

import java.util.List;

public final class DoctorForecastHtmlWriter {

    public String render(ForecastSnapshot forecast) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width,initial-scale=1">
                  <title>MAPAF Predictive Intelligence</title>
                  <style>
                    :root{--orange:#d04a02;--dark:#1d252d;--muted:#667085;--bg:#f3f5f7;--surface:#fff;--border:#dde2e7;--green:#147d64;--amber:#a15c00;--red:#c62828}
                    *{box-sizing:border-box}body{margin:0;font-family:Inter,Arial,sans-serif;background:var(--bg);color:var(--dark)}
                    header{padding:34px 5vw;color:white;background:linear-gradient(118deg,#1d252d,#394650)}
                    .brand{color:#ff9859;font-size:12px;font-weight:850;letter-spacing:2px}main{max-width:1450px;margin:auto;padding:28px 5vw 60px}
                    .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:14px}.card{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:19px;box-shadow:0 4px 15px rgba(16,24,40,.05)}
                    .label{color:var(--muted);font-size:11px;font-weight:850;letter-spacing:1px;text-transform:uppercase}.metric{margin-top:8px;font-size:30px;font-weight:880}
                    .low,.improving{color:var(--green)}.medium,.stable{color:var(--amber)}.high,.critical,.declining{color:var(--red)}section{margin-top:26px}ul{line-height:1.7}.summary{font-size:17px;line-height:1.55}
                  </style>
                </head>
                <body>
                  <header><div class="brand">MAPAF INTELLIGENT QUALITY OPERATIONS</div><h1>Risk Forecast Engine</h1><div>Explainable predictive quality intelligence</div></header>
                  <main>
                    <section class="grid">
                      {{METRICS}}
                    </section>
                    <section class="card"><div class="label">Forecast Summary</div><p class="summary">{{SUMMARY}}</p></section>
                    <section class="grid">
                      <article class="card"><div class="label">Evidence Factors</div>{{EVIDENCE}}</article>
                      <article class="card"><div class="label">Forecast Warnings</div>{{WARNINGS}}</article>
                    </section>
                    <section class="card"><div class="label">Model Transparency</div><p>This deterministic forecast uses recent weighted slope, score volatility, probe-distribution changes, readiness transitions, and historical evidence volume. No black-box machine-learning model is used.</p></section>
                  </main>
                </body>
                </html>
                """
                .replace("{{METRICS}}", metrics(forecast))
                .replace("{{SUMMARY}}", summary(forecast))
                .replace("{{EVIDENCE}}", list(forecast.evidenceFactors(), "No evidence factors available."))
                .replace("{{WARNINGS}}", list(forecast.forecastWarnings(), "No forecast warnings identified."));
    }

    private String metrics(ForecastSnapshot f) {
        return metric("Current Health", f.currentHealthScore() + "%", "")
                + metric("Predicted Health", f.predictedHealthScore() + "%", scoreClass(f.predictedHealthScore()))
                + metric("Forecast Direction", f.forecastDirection().name(), f.forecastDirection().name().toLowerCase())
                + metric("Release Risk", f.releaseRisk().name(), f.releaseRisk().name().toLowerCase())
                + metric("Confidence", f.forecastConfidencePercent() + "%", "")
                + metric("Decline Probability", f.declineProbabilityPercent() + "%", probabilityClass(f.declineProbabilityPercent()))
                + metric("Platform Ready Forecast", f.platformReadyPrediction() ? "YES" : "NO", f.platformReadyPrediction() ? "low" : "critical")
                + metric("Input Executions", String.valueOf(f.inputRecords()), "");
    }

    private String summary(ForecastSnapshot f) {
        return "MAPAF predicts a health score of " + f.predictedHealthScore() + "% ("
                + signed(f.predictedHealthDelta()) + ") with " + f.forecastConfidencePercent()
                + "% confidence. Expected release risk is " + f.releaseRisk()
                + ", and platform readiness is predicted to be "
                + (f.platformReadyPrediction() ? "maintained." : "at risk.");
    }

    private String metric(String label, String value, String css) {
        return "<article class=\"card\"><div class=\"label\">" + escape(label)
                + "</div><div class=\"metric " + css + "\">" + escape(value) + "</div></article>";
    }

    private String list(List<String> values, String empty) {
        if (values == null || values.isEmpty()) return "<p>" + escape(empty) + "</p>";
        StringBuilder out = new StringBuilder("<ul>");
        values.forEach(value -> out.append("<li>").append(escape(value)).append("</li>"));
        return out.append("</ul>").toString();
    }

    private String scoreClass(int score) { return score >= 85 ? "low" : score >= 70 ? "medium" : "high"; }
    private String probabilityClass(int value) { return value < 40 ? "low" : value < 65 ? "medium" : "high"; }
    private String signed(int value) { return value > 0 ? "+" + value : String.valueOf(value); }
    private String escape(Object value) {
        return value == null ? "" : value.toString().replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
}
