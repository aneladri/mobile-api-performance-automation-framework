package dashboard.enterprise.doctor.report;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthStatus;

public final class DoctorHtmlWriter {

    public String render(DoctorSnapshot snapshot) {
        StringBuilder probes = new StringBuilder();

        for (HealthProbeResult result : snapshot.probeResults()) {
            probes.append(renderProbe(result));
        }

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport"
                        content="width=device-width,initial-scale=1">
                  <title>MAPAF Doctor</title>
                  <style>
                    :root{
                      --orange:#d04a02;
                      --dark:#1d252d;
                      --muted:#667085;
                      --bg:#f4f6f8;
                      --surface:#fff;
                      --green:#147d64;
                      --amber:#a15c00;
                      --red:#c62828;
                      --blue:#175cd3;
                      --border:#dfe3e8;
                    }
                    *{box-sizing:border-box}
                    body{
                      margin:0;
                      background:var(--bg);
                      color:var(--dark);
                      font-family:Inter,Arial,sans-serif
                    }
                    header{
                      color:#fff;
                      padding:30px 5vw;
                      background:linear-gradient(115deg,#1d252d,#3b4650)
                    }
                    .brand{
                      color:#ff8f4c;
                      font-size:12px;
                      font-weight:850;
                      letter-spacing:2px
                    }
                    header h1{margin:8px 0}
                    nav{
                      background:#fff;
                      border-bottom:1px solid var(--border);
                      padding:11px 5vw;
                      display:flex;
                      gap:8px;
                      flex-wrap:wrap
                    }
                    nav a{
                      padding:8px 12px;
                      border:1px solid var(--border);
                      border-radius:7px;
                      text-decoration:none;
                      color:var(--dark);
                      font-size:13px;
                      font-weight:750
                    }
                    nav a.active{
                      color:var(--orange);
                      border-color:var(--orange)
                    }
                    main{
                      max-width:1500px;
                      margin:auto;
                      padding:28px 5vw 60px
                    }
                    .grid{
                      display:grid;
                      grid-template-columns:
                        repeat(auto-fit,minmax(220px,1fr));
                      gap:14px
                    }
                    .card{
                      background:var(--surface);
                      border:1px solid var(--border);
                      border-radius:13px;
                      padding:19px;
                      box-shadow:0 4px 14px rgba(16,24,40,.05)
                    }
                    .label{
                      color:var(--muted);
                      font-size:11px;
                      font-weight:850;
                      letter-spacing:1px;
                      text-transform:uppercase
                    }
                    .metric{
                      margin-top:8px;
                      font-size:30px;
                      font-weight:850
                    }
                    .healthy,.pass{color:var(--green)}
                    .degraded,.warn{color:var(--amber)}
                    .unhealthy,.fail{color:var(--red)}
                    .not-configured,.skipped{color:var(--blue)}
                    section{margin-top:28px}
                    .summary{
                      border-left:7px solid var(--green)
                    }
                    .summary.degraded{
                      border-left-color:var(--amber)
                    }
                    .summary.unhealthy{
                      border-left-color:var(--red)
                    }
                    .summary.not-configured{
                      border-left-color:var(--blue)
                    }
                    .probe{
                      margin-top:14px
                    }
                    .probe-head{
                      display:flex;
                      justify-content:space-between;
                      gap:18px
                    }
                    .badge{
                      height:max-content;
                      padding:6px 9px;
                      border-radius:6px;
                      background:#eef2f5;
                      font-size:11px;
                      font-weight:850
                    }
                    .checks{
                      margin-top:15px;
                      display:grid;
                      gap:10px
                    }
                    .check{
                      border-top:1px solid var(--border);
                      padding-top:10px
                    }
                    .check h4{margin:5px 0}
                    .check p{
                      margin:4px 0;
                      color:var(--muted);
                      line-height:1.45
                    }
                    ul{line-height:1.7}
                  </style>
                </head>
                <body>
                  <header>
                    <div class="brand">
                      MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM
                    </div>
                    <h1>MAPAF Doctor</h1>
                    <div>
                      Platform health and environment diagnostics ·
                      Version {{VERSION}}
                    </div>
                  </header>

                  <nav>
                    <a href="../index.html">Command Center</a>
                    <a href="../release-readiness.html">
                      Release Readiness
                    </a>
                    <a class="active" href="doctor-report.html">
                      MAPAF Doctor
                    </a>
                    <a href="../../../build/allure-report/index.html">
                      Evidence Hub
                    </a>
                  </nav>

                  <main>
                    <section class="card summary {{OVERALL_CLASS}}">
                      <div class="label">Overall Platform Health</div>
                      <div class="metric {{OVERALL_CLASS}}">
                        {{OVERALL_STATUS}}
                      </div>
                      <p>{{EXECUTIVE_SUMMARY}}</p>
                    </section>

                    <section class="grid">
                      {{METRICS}}
                    </section>

                    <section>
                      <h2>Health Probes</h2>
                      {{PROBES}}
                    </section>

                    <section class="grid">
                      <article class="card">
                        <div class="label">Platform Diagnoses</div>
                        {{DIAGNOSES}}
                      </article>

                      <article class="card">
                        <div class="label">Corrective Actions</div>
                        {{ACTIONS}}
                      </article>
                    </section>
                  </main>
                </body>
                </html>
                """
                .replace(
                        "{{VERSION}}",
                        escape(snapshot.platformVersion())
                )
                .replace(
                        "{{OVERALL_STATUS}}",
                        escape(snapshot.overallStatus())
                )
                .replace(
                        "{{OVERALL_CLASS}}",
                        css(snapshot.overallStatus())
                )
                .replace(
                        "{{EXECUTIVE_SUMMARY}}",
                        escape(snapshot.executiveSummary())
                )
                .replace(
                        "{{METRICS}}",
                        renderMetrics(snapshot)
                )
                .replace(
                        "{{PROBES}}",
                        probes.toString()
                )
                .replace(
                        "{{DIAGNOSES}}",
                        renderList(snapshot.platformDiagnoses())
                )
                .replace(
                        "{{ACTIONS}}",
                        renderList(snapshot.correctiveActions())
                );
    }

    private String renderMetrics(DoctorSnapshot snapshot) {
        return metric(
                "Health Score",
                snapshot.healthScore() + "%",
                scoreClass(snapshot.healthScore())
        ) + metric(
                "Platform Ready",
                snapshot.platformReady() ? "YES" : "NO",
                snapshot.platformReady() ? "healthy" : "unhealthy"
        ) + metric(
                "Healthy Probes",
                snapshot.healthyProbes()
                        + "/"
                        + snapshot.totalProbes(),
                "healthy"
        ) + metric(
                "Degraded Probes",
                String.valueOf(snapshot.degradedProbes()),
                snapshot.degradedProbes() == 0
                        ? "healthy"
                        : "degraded"
        ) + metric(
                "Unhealthy Probes",
                String.valueOf(snapshot.unhealthyProbes()),
                snapshot.unhealthyProbes() == 0
                        ? "healthy"
                        : "unhealthy"
        ) + metric(
                "Not Configured",
                String.valueOf(snapshot.notConfiguredProbes()),
                snapshot.notConfiguredProbes() == 0
                        ? "healthy"
                        : "not-configured"
        );
    }

    private String renderProbe(HealthProbeResult result) {
        StringBuilder checks = new StringBuilder();

        for (DiagnosticCheck check : result.checks()) {
            checks.append(renderCheck(check));
        }

        return "<article class=\"card probe\">"
                + "<div class=\"probe-head\">"
                + "<div><div class=\"label\">Health Probe</div>"
                + "<h3>"
                + escape(result.probeName())
                + "</h3></div>"
                + "<span class=\"badge\">"
                + escape(result.severity())
                + "</span></div>"
                + "<div class=\"metric "
                + css(result.status())
                + "\">"
                + escape(result.status())
                + "</div>"
                + "<p>"
                + escape(result.summary())
                + "</p>"
                + "<p><strong>Diagnosis:</strong> "
                + escape(result.diagnosis())
                + "</p>"
                + "<p><strong>Duration:</strong> "
                + result.durationMillis()
                + " ms</p>"
                + "<div class=\"checks\">"
                + checks
                + "</div>"
                + "</article>";
    }

    private String renderCheck(DiagnosticCheck check) {
        return "<div class=\"check\">"
                + "<div class=\"label\">Diagnostic Check</div>"
                + "<h4>"
                + escape(check.name())
                + " · <span class=\""
                + check.status().name().toLowerCase()
                + "\">"
                + escape(check.status())
                + "</span></h4>"
                + "<p><strong>Expected:</strong> "
                + escape(check.expected())
                + "</p>"
                + "<p><strong>Actual:</strong> "
                + escape(check.actual())
                + "</p>"
                + "</div>";
    }

    private String metric(
            String label,
            String value,
            String css
    ) {
        return "<article class=\"card\"><div class=\"label\">"
                + escape(label)
                + "</div><div class=\"metric "
                + css
                + "\">"
                + escape(value)
                + "</div></article>";
    }

    private String renderList(java.util.List<String> values) {
        if (values == null || values.isEmpty()) {
            return "<p>No issues identified.</p>";
        }

        StringBuilder html = new StringBuilder("<ul>");

        for (String value : values) {
            html.append("<li>")
                    .append(escape(value))
                    .append("</li>");
        }

        return html.append("</ul>").toString();
    }

    private String css(HealthStatus status) {
        return status.name()
                .toLowerCase()
                .replace('_', '-');
    }

    private String scoreClass(int score) {
        if (score >= 85) {
            return "healthy";
        }

        if (score >= 60) {
            return "degraded";
        }

        return "unhealthy";
    }

    private String escape(Object value) {
        if (value == null) {
            return "";
        }

        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
