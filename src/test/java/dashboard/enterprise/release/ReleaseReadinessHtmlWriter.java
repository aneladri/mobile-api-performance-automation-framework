package dashboard.enterprise.release;

import dashboard.enterprise.release.decision.ExecutiveDecision;
import dashboard.enterprise.release.gates.QualityGateResult;

public final class ReleaseReadinessHtmlWriter {

    public String render(ReleaseReadinessSnapshot snapshot) {
        ExecutiveDecision decision = snapshot.executiveDecision();

        StringBuilder gateCards = new StringBuilder();

        for (QualityGateResult gate : snapshot.qualityGateResults()) {
            gateCards.append(renderGate(gate));
        }

        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport"
                        content="width=device-width,initial-scale=1">
                  <title>MAPAF Executive Release Decision</title>
                  <style>
                    :root{
                      --orange:#d04a02;
                      --dark:#1d252d;
                      --muted:#667085;
                      --bg:#f4f6f8;
                      --surface:#fff;
                      --green:#147d64;
                      --red:#c62828;
                      --amber:#a15c00;
                      --border:#dfe3e8;
                    }
                    *{box-sizing:border-box}
                    body{
                      margin:0;
                      font-family:Inter,Arial,sans-serif;
                      background:var(--bg);
                      color:var(--dark)
                    }
                    header{
                      padding:30px 5vw;
                      color:#fff;
                      background:linear-gradient(115deg,#1d252d,#3b4650)
                    }
                    .brand{
                      color:#ff8f4c;
                      font-size:12px;
                      letter-spacing:2px;
                      font-weight:850
                    }
                    header h1{margin:8px 0}
                    nav{
                      background:#fff;
                      border-bottom:1px solid var(--border);
                      padding:11px 5vw;
                      display:flex;
                      gap:8px;
                      flex-wrap:wrap;
                      position:sticky;
                      top:0;
                      z-index:5
                    }
                    nav a{
                      padding:8px 12px;
                      border:1px solid var(--border);
                      border-radius:7px;
                      color:var(--dark);
                      text-decoration:none;
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
                    .pass{color:var(--green)}
                    .warn{color:var(--amber)}
                    .fail{color:var(--red)}
                    section{margin-top:28px}
                    .decision{
                      border-left:7px solid var(--green);
                      padding:27px
                    }
                    .decision.warn{
                      border-left-color:var(--amber)
                    }
                    .decision.fail{
                      border-left-color:var(--red)
                    }
                    .decision h2{
                      margin:10px 0;
                      font-size:36px
                    }
                    .gate{
                      display:flex;
                      flex-direction:column;
                      justify-content:space-between;
                      min-height:330px
                    }
                    .gate-top{
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
                    dl{
                      display:grid;
                      grid-template-columns:1fr;
                      gap:9px;
                      margin:16px 0
                    }
                    dl div{
                      border-top:1px solid var(--border);
                      padding-top:8px
                    }
                    dt{
                      color:var(--muted);
                      font-size:11px;
                      font-weight:850;
                      text-transform:uppercase
                    }
                    dd{
                      margin:4px 0 0;
                      font-weight:650;
                      line-height:1.4
                    }
                    ul{line-height:1.7}
                  </style>
                </head>
                <body>
                <header>
                  <div class="brand">
                    MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM
                  </div>
                  <h1>Executive Release Decision</h1>
                  <div>
                    {{APPLICATION}} · {{ENVIRONMENT}} ·
                    Policy-driven release governance
                  </div>
                </header>

                <nav>
                  <a href="index.html">Command Center</a>
                  <a href="mobile.html">Mobile</a>
                  <a href="web.html">Portal</a>
                  <a href="api.html">API</a>
                  <a href="performance.html">Performance</a>
                  <a class="active"
                     href="release-readiness.html">
                    Release Readiness
                  </a>
                  <a href="../../build/allure-report/index.html">
                    Evidence Hub
                  </a>
                </nav>

                <main>
                  <section class="card decision {{DECISION_CLASS}}">
                    <div class="label">MAPAF Executive Recommendation</div>
                    <h2 class="{{DECISION_CLASS}}">
                      {{RECOMMENDATION}}
                    </h2>
                    <p>
                      Decision confidence:
                      <strong>{{CONFIDENCE}}%</strong>
                      · Release risk:
                      <strong>{{RISK}}</strong>
                    </p>
                    <p>
                      Human approval required:
                      <strong>{{HUMAN_APPROVAL}}</strong>
                    </p>
                  </section>

                  <section class="grid">
                    {{METRICS}}
                  </section>

                  <section>
                    <h2>Governed Quality Gates</h2>
                    <div class="grid">
                      {{GATES}}
                    </div>
                  </section>

                  <section class="grid">
                    <article class="card">
                      <div class="label">Decision Reasons</div>
                      {{REASONS}}
                    </article>

                    <article class="card">
                      <div class="label">Blocking Issues</div>
                      {{BLOCKERS}}
                    </article>

                    <article class="card">
                      <div class="label">Advisory Concerns</div>
                      {{CONCERNS}}
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
                        "{{APPLICATION}}",
                        escape(snapshot.application())
                )
                .replace(
                        "{{ENVIRONMENT}}",
                        escape(snapshot.environment())
                )
                .replace(
                        "{{RECOMMENDATION}}",
                        escape(decision.recommendation())
                )
                .replace(
                        "{{CONFIDENCE}}",
                        String.valueOf(decision.decisionConfidence())
                )
                .replace(
                        "{{RISK}}",
                        escape(decision.releaseRisk())
                )
                .replace(
                        "{{HUMAN_APPROVAL}}",
                        decision.humanApprovalRequired()
                                ? "YES"
                                : "NO"
                )
                .replace(
                        "{{DECISION_CLASS}}",
                        decisionClass(decision)
                )
                .replace(
                        "{{METRICS}}",
                        renderMetrics(snapshot)
                )
                .replace(
                        "{{GATES}}",
                        gateCards.toString()
                )
                .replace(
                        "{{REASONS}}",
                        renderList(decision.decisionReasons())
                )
                .replace(
                        "{{BLOCKERS}}",
                        renderList(decision.blockingIssues())
                )
                .replace(
                        "{{CONCERNS}}",
                        renderList(decision.advisoryConcerns())
                )
                .replace(
                        "{{ACTIONS}}",
                        renderList(decision.correctiveActions())
                );
    }

    private String renderMetrics(
            ReleaseReadinessSnapshot snapshot
    ) {
        return metric(
                "Readiness Score",
                snapshot.readinessScore() + "%",
                scoreClass(snapshot.readinessScore())
        ) + metric(
                "Blocking Gates",
                snapshot.blockingGatesPassed()
                        + "/"
                        + snapshot.blockingGatesTotal(),
                snapshot.releaseBlocked() ? "fail" : "pass"
        ) + metric(
                "Non-blocking Gates",
                snapshot.advisoryGatesPassed()
                        + "/"
                        + snapshot.advisoryGatesTotal(),
                snapshot.advisoryGatesPassed()
                        == snapshot.advisoryGatesTotal()
                        ? "pass"
                        : "warn"
        ) + metric(
                "Evidence Coverage",
                snapshot.evidenceCoveragePercent() + "%",
                scoreClass(snapshot.evidenceCoveragePercent())
        ) + metric(
                "AI Readiness",
                snapshot.aiReadinessPercent() + "%",
                scoreClass(snapshot.aiReadinessPercent())
        ) + metric(
                "Business Readiness",
                snapshot.businessReadinessPercent() + "%",
                scoreClass(snapshot.businessReadinessPercent())
        );
    }

    private String renderGate(QualityGateResult gate) {
        String css = statusClass(gate.status().name());

        return "<article class=\"card gate\">"
                + "<div>"
                + "<div class=\"gate-top\">"
                + "<div><div class=\"label\">Governed Quality Gate</div>"
                + "<h3>"
                + escape(gate.gateName())
                + "</h3></div>"
                + "<span class=\"badge\">"
                + escape(gate.severity())
                + "</span></div>"
                + "<div class=\"metric "
                + css
                + "\">"
                + escape(gate.status())
                + "</div>"
                + "<dl>"
                + detail("Score", gate.score() + "/100")
                + detail("Policy", gate.threshold())
                + detail("Actual", gate.actual())
                + detail("Rationale", gate.rationale())
                + detail("Action", gate.recommendation())
                + "</dl></div>"
                + "<div class=\"label\">Policy "
                + escape(gate.policyVersion())
                + "</div>"
                + "</article>";
    }

    private String detail(String label, Object value) {
        return "<div><dt>"
                + escape(label)
                + "</dt><dd>"
                + escape(value)
                + "</dd></div>";
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

    private String decisionClass(ExecutiveDecision decision) {
        return switch (decision.status()) {
            case READY -> "pass";
            case CONDITIONAL -> "warn";
            case BLOCKED -> "fail";
        };
    }

    private String statusClass(String status) {
        return switch (status.toUpperCase()) {
            case "PASS" -> "pass";
            case "WARN", "REVIEW" -> "warn";
            default -> "fail";
        };
    }

    private String scoreClass(int score) {
        if (score >= 85) {
            return "pass";
        }

        if (score >= 70) {
            return "warn";
        }

        return "fail";
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
