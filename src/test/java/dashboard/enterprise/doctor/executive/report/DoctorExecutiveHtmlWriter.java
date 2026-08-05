package dashboard.enterprise.doctor.executive.report;

import dashboard.enterprise.doctor.executive.model.DoctorExecutiveOverview;
import dashboard.enterprise.doctor.executive.model.DoctorReadinessSummary;
import dashboard.enterprise.doctor.executive.model.ExecutiveProbeSummary;
import dashboard.enterprise.doctor.model.HealthStatus;

import java.util.List;

public final class DoctorExecutiveHtmlWriter {

    public String render(
            DoctorExecutiveOverview overview
    ) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport"
                        content="width=device-width,initial-scale=1">
                  <title>MAPAF Doctor Overview</title>
                  <style>
                    :root {
                      --orange:#d04a02;
                      --dark:#1d252d;
                      --muted:#667085;
                      --bg:#f3f5f7;
                      --surface:#ffffff;
                      --border:#dde2e7;
                      --green:#147d64;
                      --amber:#a15c00;
                      --red:#c62828;
                      --blue:#175cd3;
                    }
                    * { box-sizing:border-box; }
                    body {
                      margin:0;
                      font-family:Inter,Arial,sans-serif;
                      background:var(--bg);
                      color:var(--dark);
                    }
                    header {
                      padding:32px 5vw;
                      color:#fff;
                      background:
                        linear-gradient(118deg,#1d252d,#394650);
                    }
                    .brand {
                      color:#ff9859;
                      font-size:12px;
                      font-weight:850;
                      letter-spacing:2px;
                    }
                    h1 { margin:8px 0 5px; }
                    nav {
                      display:flex;
                      flex-wrap:wrap;
                      gap:8px;
                      padding:11px 5vw;
                      background:#fff;
                      border-bottom:1px solid var(--border);
                    }
                    nav a {
                      padding:8px 12px;
                      text-decoration:none;
                      color:var(--dark);
                      border:1px solid var(--border);
                      border-radius:7px;
                      font-size:13px;
                      font-weight:750;
                    }
                    nav a.active {
                      color:var(--orange);
                      border-color:var(--orange);
                    }
                    main {
                      max-width:1500px;
                      margin:auto;
                      padding:28px 5vw 60px;
                    }
                    .grid {
                      display:grid;
                      grid-template-columns:
                        repeat(auto-fit,minmax(210px,1fr));
                      gap:14px;
                    }
                    .card {
                      background:var(--surface);
                      border:1px solid var(--border);
                      border-radius:14px;
                      padding:19px;
                      box-shadow:0 4px 15px rgba(16,24,40,.05);
                    }
                    .hero {
                      display:grid;
                      grid-template-columns:
                        minmax(260px,1.3fr)
                        repeat(3,minmax(150px,.7fr));
                      gap:14px;
                    }
                    .label {
                      color:var(--muted);
                      font-size:11px;
                      font-weight:850;
                      letter-spacing:1px;
                      text-transform:uppercase;
                    }
                    .metric {
                      margin-top:8px;
                      font-size:31px;
                      font-weight:880;
                    }
                    .healthy { color:var(--green); }
                    .degraded { color:var(--amber); }
                    .unhealthy { color:var(--red); }
                    .not-configured { color:var(--blue); }
                    .probe-card {
                      display:block;
                      color:inherit;
                      text-decoration:none;
                      transition:transform .15s ease;
                    }
                    .probe-card:hover {
                      transform:translateY(-2px);
                    }
                    .probe-head {
                      display:flex;
                      justify-content:space-between;
                      gap:12px;
                    }
                    .badge {
                      padding:5px 8px;
                      background:#eef2f5;
                      border-radius:6px;
                      font-size:10px;
                      font-weight:850;
                      height:max-content;
                    }
                    .probe-card p {
                      color:var(--muted);
                      line-height:1.45;
                    }
                    section { margin-top:28px; }
                    .ready {
                      font-weight:850;
                      color:var(--green);
                    }
                    .not-ready {
                      font-weight:850;
                      color:var(--red);
                    }
                    ul { line-height:1.7; }
                    footer {
                      color:var(--muted);
                      font-size:12px;
                      margin-top:28px;
                    }
                    @media(max-width:900px) {
                      .hero {
                        grid-template-columns:
                          repeat(auto-fit,minmax(220px,1fr));
                      }
                    }
                  </style>
                </head>
                <body>
                  <header>
                    <div class="brand">
                      MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM
                    </div>
                    <h1>MAPAF Doctor Overview</h1>
                    <div>
                      Executive platform operations and health intelligence
                    </div>
                  </header>

                  <nav>
                    <a href="../index.html">Command Center</a>
                    <a href="../release-readiness.html">
                      Release Readiness
                    </a>
                    <a class="active" href="doctor-overview.html">
                      Doctor Overview
                    </a>
                    <a href="doctor-diagnosis.html">
                      Diagnosis Center
                    </a>
                    <a href="executive-report.html">
                      Executive Report
                    </a>
                    <a href="doctor-report.html">
                      Doctor Details
                    </a>
                    <a href="../../../build/allure-report/index.html">
                      Evidence Hub
                    </a>
                    <a href="../mobile.html">Mobile</a>
                    <a href="../api.html">API</a>
                    <a href="../performance.html">Performance</a>
                  </nav>

                  <main>
                    <section class="hero">
                      <article class="card">
                        <div class="label">
                          Overall Platform Health
                        </div>
                        <div class="metric {{STATUS_CLASS}}">
                          {{OVERALL_STATUS}}
                        </div>
                        <p>{{EXECUTIVE_SUMMARY}}</p>
                      </article>

                      {{HERO_METRICS}}
                    </section>

                    <section>
                      <h2>Health Distribution</h2>
                      <div class="grid">
                        {{DISTRIBUTION}}
                      </div>
                    </section>

                    <section>
                      <h2>Platform Readiness</h2>
                      <div class="grid">
                        {{READINESS}}
                      </div>
                    </section>

                    <section>
                      <h2>Capability Health</h2>
                      <div class="grid">
                        {{PROBES}}
                      </div>
                    </section>

                    <section class="grid">
                      <article class="card">
                        <div class="label">
                          Priority Issues
                        </div>
                        {{ISSUES}}
                      </article>

                      <article class="card">
                        <div class="label">
                          Recommended Actions
                        </div>
                        {{ACTIONS}}
                      </article>
                    </section>

                    <footer>
                      Contract {{SCHEMA}} · Version {{VERSION}} ·
                      Generated {{GENERATED_AT}}
                    </footer>
                  </main>
                </body>
                </html>
                """
                .replace(
                        "{{STATUS_CLASS}}",
                        css(overview.overallStatus())
                )
                .replace(
                        "{{OVERALL_STATUS}}",
                        escape(overview.overallStatus())
                )
                .replace(
                        "{{EXECUTIVE_SUMMARY}}",
                        escape(overview.executiveSummary())
                )
                .replace(
                        "{{HERO_METRICS}}",
                        heroMetrics(overview)
                )
                .replace(
                        "{{DISTRIBUTION}}",
                        distribution(overview)
                )
                .replace(
                        "{{READINESS}}",
                        readiness(overview.readiness())
                )
                .replace(
                        "{{PROBES}}",
                        probes(overview.probes())
                )
                .replace(
                        "{{ISSUES}}",
                        list(overview.priorityIssues())
                )
                .replace(
                        "{{ACTIONS}}",
                        list(overview.recommendedActions())
                )
                .replace(
                        "{{SCHEMA}}",
                        escape(overview.schemaVersion())
                )
                .replace(
                        "{{VERSION}}",
                        escape(overview.platformVersion())
                )
                .replace(
                        "{{GENERATED_AT}}",
                        escape(overview.generatedAt())
                );
    }

    private String heroMetrics(
            DoctorExecutiveOverview overview
    ) {
        return metric(
                "Weighted Health Score",
                overview.weightedHealthScore() + "%",
                scoreClass(overview.weightedHealthScore())
        ) + metric(
                "Platform Ready",
                overview.platformReady() ? "YES" : "NO",
                overview.platformReady()
                        ? "healthy"
                        : "unhealthy"
        ) + metric(
                "Blocking Issues",
                String.valueOf(overview.blockingIssues()),
                overview.blockingIssues() == 0
                        ? "healthy"
                        : "unhealthy"
        );
    }

    private String distribution(
            DoctorExecutiveOverview overview
    ) {
        return metric(
                "Healthy",
                String.valueOf(overview.healthyProbes()),
                "healthy"
        ) + metric(
                "Degraded",
                String.valueOf(overview.degradedProbes()),
                overview.degradedProbes() == 0
                        ? "healthy"
                        : "degraded"
        ) + metric(
                "Unhealthy",
                String.valueOf(overview.unhealthyProbes()),
                overview.unhealthyProbes() == 0
                        ? "healthy"
                        : "unhealthy"
        ) + metric(
                "Not Configured",
                String.valueOf(
                        overview.notConfiguredProbes()
                ),
                overview.notConfiguredProbes() == 0
                        ? "healthy"
                        : "not-configured"
        );
    }

    private String readiness(
            DoctorReadinessSummary readiness
    ) {
        return readinessCard(
                "Execution Ready",
                readiness.executionReady()
        ) + readinessCard(
                "Evidence Ready",
                readiness.evidenceReady()
        ) + readinessCard(
                "Release Ready",
                readiness.releaseReady()
        ) + readinessCard(
                "AI Ready",
                readiness.aiReady()
        ) + readinessCard(
                "Mobile Ready",
                readiness.mobileReady()
        ) + readinessCard(
                "API Ready",
                readiness.apiReady()
        ) + readinessCard(
                "Performance Ready",
                readiness.performanceReady()
        ) + readinessCard(
                "Platform Ready",
                readiness.platformReady()
        );
    }

    private String probes(
            List<ExecutiveProbeSummary> probes
    ) {
        StringBuilder html = new StringBuilder();

        for (ExecutiveProbeSummary probe : probes) {
            html.append(
                    "<a class=\"card probe-card\" "
                            + "href=\"doctor-report.html#"
                            + escape(probe.probeId())
                            + "\">"
                            + "<div class=\"probe-head\">"
                            + "<div><div class=\"label\">"
                            + escape(probe.severity())
                            + " Probe</div><h3>"
                            + escape(probe.name())
                            + "</h3></div>"
                            + "<span class=\"badge\">"
                            + probe.score()
                            + "%</span></div>"
                            + "<div class=\"metric "
                            + css(probe.status())
                            + "\">"
                            + escape(probe.status())
                            + "</div>"
                            + "<p>"
                            + escape(probe.summary())
                            + "</p>"
                            + "</a>"
            );
        }

        return html.toString();
    }

    private String readinessCard(
            String name,
            boolean ready
    ) {
        return "<article class=\"card\">"
                + "<div class=\"label\">"
                + escape(name)
                + "</div><div class=\"metric "
                + (ready ? "healthy" : "unhealthy")
                + "\">"
                + (ready ? "YES" : "NO")
                + "</div></article>";
    }

    private String metric(
            String label,
            String value,
            String css
    ) {
        return "<article class=\"card\">"
                + "<div class=\"label\">"
                + escape(label)
                + "</div><div class=\"metric "
                + css
                + "\">"
                + escape(value)
                + "</div></article>";
    }

    private String list(List<String> values) {
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
