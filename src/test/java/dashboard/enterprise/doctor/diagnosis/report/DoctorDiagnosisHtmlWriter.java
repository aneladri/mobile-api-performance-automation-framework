package dashboard.enterprise.doctor.diagnosis.report;

import dashboard.enterprise.doctor.diagnosis.model.DiagnosisItem;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisSnapshot;

import java.util.Map;

public final class DoctorDiagnosisHtmlWriter {

    public String render(DiagnosisSnapshot snapshot) {
        StringBuilder cards = new StringBuilder();
        for (DiagnosisItem item : snapshot.diagnoses()) {
            cards.append(card(item));
        }

        StringBuilder impact = new StringBuilder();
        for (Map.Entry<String, String> entry : snapshot.capabilityImpact().entrySet()) {
            impact.append("<article class=\"card\"><div class=\"label\">")
                    .append(escape(entry.getKey()))
                    .append("</div><div class=\"impact\">")
                    .append(escape(entry.getValue()))
                    .append("</div></article>");
        }

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width,initial-scale=1">
                  <title>MAPAF Doctor Diagnosis Center</title>
                  <style>
                    :root{--orange:#d04a02;--dark:#1d252d;--muted:#667085;--bg:#f3f5f7;--surface:#fff;--border:#dde2e7;--green:#147d64;--amber:#a15c00;--red:#c62828;--blue:#175cd3}
                    *{box-sizing:border-box}body{margin:0;font-family:Inter,Arial,sans-serif;background:var(--bg);color:var(--dark)}
                    header{padding:32px 5vw;color:#fff;background:linear-gradient(118deg,#1d252d,#394650)}
                    .brand{color:#ff9859;font-size:12px;font-weight:850;letter-spacing:2px}h1{margin:8px 0 5px}
                    nav{display:flex;flex-wrap:wrap;gap:8px;padding:11px 5vw;background:#fff;border-bottom:1px solid var(--border)}
                    nav a{padding:8px 12px;text-decoration:none;color:var(--dark);border:1px solid var(--border);border-radius:7px;font-size:13px;font-weight:750}
                    nav a.active{color:var(--orange);border-color:var(--orange)}main{max-width:1500px;margin:auto;padding:28px 5vw 60px}
                    .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:14px}.card{background:var(--surface);border:1px solid var(--border);border-radius:14px;padding:19px;box-shadow:0 4px 15px rgba(16,24,40,.05)}
                    .hero{display:grid;grid-template-columns:minmax(280px,1.4fr) repeat(3,minmax(160px,.6fr));gap:14px}.label{color:var(--muted);font-size:11px;font-weight:850;letter-spacing:1px;text-transform:uppercase}.metric{margin-top:8px;font-size:31px;font-weight:880}.healthy{color:var(--green)}.degraded{color:var(--amber)}.unhealthy{color:var(--red)}
                    section{margin-top:28px}.diagnosis{border-left:7px solid var(--amber)}.diagnosis.blocking{border-left-color:var(--red)}.meta{display:flex;flex-wrap:wrap;gap:8px;margin:12px 0}.pill{background:#eef2f5;border-radius:999px;padding:6px 9px;font-size:11px;font-weight:800}.diagnosis p{color:var(--muted);line-height:1.5}.impact{font-size:20px;font-weight:800;margin-top:8px}ul{line-height:1.7}footer{color:var(--muted);font-size:12px;margin-top:28px}
                    @media(max-width:900px){.hero{grid-template-columns:repeat(auto-fit,minmax(220px,1fr))}}
                  </style>
                </head>
                <body>
                  <header><div class="brand">MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM</div><h1>Diagnosis &amp; Corrective Action Center</h1><div>Prioritized root causes, business impact, ownership, and recovery guidance</div></header>
                  <nav>
                    <a href="../index.html">Command Center</a>
                    <a href="../release-readiness.html">Release Readiness</a>
                    <a href="doctor-overview.html">Doctor Overview</a>
                    <a class="active" href="doctor-diagnosis.html">Diagnosis Center</a>
                    <a href="doctor-report.html">Doctor Details</a>
                    <a href="../../../build/allure-report/index.html">Evidence Hub</a>
                  </nav>
                  <main>
                    <section class="hero">
                      <article class="card"><div class="label">Executive Diagnosis</div><div class="metric">{{TOTAL}} ISSUE(S)</div><p>{{SUMMARY}}</p></article>
                      {{METRICS}}
                    </section>
                    <section><h2>Immediate Actions</h2><article class="card">{{IMMEDIATE}}</article></section>
                    <section><h2>Capability Business Impact</h2><div class="grid">{{IMPACT}}</div></section>
                    <section><h2>Prioritized Diagnoses</h2><div class="grid">{{DIAGNOSES}}</div></section>
                    <footer>Contract {{SCHEMA}} · Version {{VERSION}} · Generated {{GENERATED}}</footer>
                  </main>
                </body>
                </html>
                """
                .replace("{{TOTAL}}", String.valueOf(snapshot.totalIssues()))
                .replace("{{SUMMARY}}", escape(snapshot.executiveSummary()))
                .replace("{{METRICS}}", metrics(snapshot))
                .replace("{{IMMEDIATE}}", list(snapshot.immediateActions()))
                .replace("{{IMPACT}}", impact.toString())
                .replace("{{DIAGNOSES}}", cards.length() == 0 ? "<article class=\"card\"><p>No issues identified.</p></article>" : cards.toString())
                .replace("{{SCHEMA}}", escape(snapshot.schemaVersion()))
                .replace("{{VERSION}}", escape(snapshot.platformVersion()))
                .replace("{{GENERATED}}", escape(snapshot.generatedAt()));
    }

    private String metrics(DiagnosisSnapshot snapshot) {
        return metric("Blocking Issues", snapshot.blockingIssues(), snapshot.blockingIssues() == 0 ? "healthy" : "unhealthy")
                + metric("Warnings", snapshot.warnings(), snapshot.warnings() == 0 ? "healthy" : "degraded")
                + metric("Open Actions", snapshot.openActions(), snapshot.openActions() == 0 ? "healthy" : "degraded");
    }

    private String metric(String label, int value, String css) {
        return "<article class=\"card\"><div class=\"label\">" + escape(label) + "</div><div class=\"metric " + css + "\">" + value + "</div></article>";
    }

    private String card(DiagnosisItem item) {
        return "<article class=\"card diagnosis " + (item.blocking() ? "blocking" : "") + "\">"
                + "<div class=\"label\">" + escape(item.priority()) + " · " + escape(item.capability()) + "</div>"
                + "<h3>" + escape(item.issue()) + "</h3>"
                + "<div class=\"meta\"><span class=\"pill\">" + escape(item.severity()) + "</span><span class=\"pill\">Owner: " + escape(item.owner()) + "</span><span class=\"pill\">Effort: " + escape(item.estimatedEffort()) + "</span><span class=\"pill\">" + escape(item.actionStatus()) + "</span></div>"
                + "<p><strong>Root cause:</strong> " + escape(item.rootCause()) + "</p>"
                + "<p><strong>Technical impact:</strong> " + escape(item.technicalImpact()) + "</p>"
                + "<p><strong>Business impact:</strong> " + escape(item.businessImpact()) + "</p>"
                + "<p><strong>Recommended action:</strong> " + escape(item.recommendedAction()) + "</p>"
                + "</article>";
    }

    private String list(java.util.List<String> values) {
        if (values == null || values.isEmpty()) return "<p>No immediate action is required.</p>";
        StringBuilder html = new StringBuilder("<ul>");
        for (String value : values) html.append("<li>").append(escape(value)).append("</li>");
        return html.append("</ul>").toString();
    }

    private String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
