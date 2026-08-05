package dashboard.enterprise.intelligence.trend.report;

import dashboard.enterprise.intelligence.trend.model.ReleaseTrendSummary;
import dashboard.enterprise.intelligence.trend.model.TrendPoint;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

public final class DoctorTrendHtmlWriter {

    public String render(TrendSnapshot trend) {
        return """
                <!doctype html><html lang="en"><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <title>MAPAF Trend Intelligence</title>
                <style>
                body{font-family:Arial,sans-serif;margin:0;background:#f4f6f8;color:#1d252d}
                header{padding:28px 5vw;background:#1d252d;color:white}main{max-width:1400px;margin:auto;padding:28px 5vw}
                .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:14px}.card{background:white;border:1px solid #d8dee4;border-radius:12px;padding:18px}
                .metric{font-size:30px;font-weight:800;margin-top:8px}.label{font-size:11px;text-transform:uppercase;color:#667085;font-weight:800;letter-spacing:1px}
                table{width:100%;border-collapse:collapse;background:white}th,td{text-align:left;padding:11px;border-bottom:1px solid #e5e7eb}th{background:#eef1f4}
                .IMPROVING{color:#147d64}.STABLE{color:#175cd3}.DECLINING{color:#c62828}.INSUFFICIENT_DATA{color:#a15c00}
                ul{line-height:1.6}</style></head><body>
                <header><div>MAPAF ENTERPRISE QUALITY ENGINEERING PLATFORM</div><h1>Trend Intelligence</h1></header>
                <main><section class="grid">
                <article class="card"><div class="label">Trend</div><div class="metric {{DIRECTION}}">{{DIRECTION}}</div></article>
                <article class="card"><div class="label">Current Score</div><div class="metric">{{CURRENT_SCORE}}%</div></article>
                <article class="card"><div class="label">Score Delta</div><div class="metric">{{SCORE_DELTA}}</div></article>
                <article class="card"><div class="label">History Records</div><div class="metric">{{TOTAL}}</div></article>
                </section>
                <section><h2>Regression Signals</h2>{{REGRESSIONS}}</section>
                <section><h2>Improvement Signals</h2>{{IMPROVEMENTS}}</section>
                <section><h2>Execution Timeline</h2><table><thead><tr><th>Captured</th><th>Release</th><th>Execution</th><th>Status</th><th>Score</th><th>Ready</th></tr></thead><tbody>{{TIMELINE}}</tbody></table></section>
                <section><h2>Release Summary</h2><table><thead><tr><th>Release</th><th>Executions</th><th>Latest</th><th>Average</th><th>Range</th><th>Trend</th></tr></thead><tbody>{{RELEASES}}</tbody></table></section>
                <p>Contract {{SCHEMA}} · Generated {{GENERATED}}</p></main></body></html>
                """
                .replace("{{DIRECTION}}", trend.direction().name())
                .replace("{{CURRENT_SCORE}}", String.valueOf(trend.currentHealthScore()))
                .replace("{{SCORE_DELTA}}", signed(trend.healthScoreDelta()))
                .replace("{{TOTAL}}", String.valueOf(trend.totalRecords()))
                .replace("{{REGRESSIONS}}", list(trend.regressions(), "No regression signals detected."))
                .replace("{{IMPROVEMENTS}}", list(trend.improvements(), "No improvement signals detected."))
                .replace("{{TIMELINE}}", timeline(trend))
                .replace("{{RELEASES}}", releases(trend))
                .replace("{{SCHEMA}}", escape(trend.schemaVersion()))
                .replace("{{GENERATED}}", escape(trend.generatedAt()));
    }

    private String timeline(TrendSnapshot trend) {
        StringBuilder html = new StringBuilder();
        for (TrendPoint point : trend.timeline()) {
            html.append("<tr><td>").append(escape(point.capturedAt())).append("</td><td>")
                    .append(escape(point.releaseId())).append("</td><td>")
                    .append(escape(point.executionId())).append("</td><td>")
                    .append(point.overallStatus()).append("</td><td>")
                    .append(point.healthScore()).append("%</td><td>")
                    .append(point.platformReady() ? "YES" : "NO").append("</td></tr>");
        }
        return html.toString();
    }

    private String releases(TrendSnapshot trend) {
        StringBuilder html = new StringBuilder();
        for (ReleaseTrendSummary release : trend.releases()) {
            html.append("<tr><td>").append(escape(release.releaseId())).append("</td><td>")
                    .append(release.executions()).append("</td><td>")
                    .append(release.latestHealthScore()).append("%</td><td>")
                    .append(String.format("%.1f%%", release.averageHealthScore())).append("</td><td>")
                    .append(release.minimumHealthScore()).append("–").append(release.maximumHealthScore())
                    .append("</td><td class=\"").append(release.direction()).append("\">")
                    .append(release.direction()).append("</td></tr>");
        }
        return html.toString();
    }

    private String list(java.util.List<String> values, String empty) {
        if (values.isEmpty()) return "<div class=\"card\">" + escape(empty) + "</div>";
        StringBuilder html = new StringBuilder("<div class=\"card\"><ul>");
        for (String value : values) html.append("<li>").append(escape(value)).append("</li>");
        return html.append("</ul></div>").toString();
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String escape(Object value) {
        return value == null ? "" : value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
