package dashboard.enterprise.reporting;

import dashboard.enterprise.model.EnterpriseModuleView;
import dashboard.enterprise.model.ExecutiveDashboardView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ExecutiveDashboardHtmlWriter {

    public Path write(ExecutiveDashboardView view, Path output) throws IOException {
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.writeString(output, render(view), StandardCharsets.UTF_8);
        return output.toAbsolutePath();
    }

    String render(ExecutiveDashboardView view) {
        StringBuilder modules = new StringBuilder();
        for (EnterpriseModuleView module : view.modules()) {
            modules.append(renderModule(module));
        }

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>MAPAF Enterprise Command Center</title>
                  <style>
                    :root { --orange:#d04a02; --dark:#1d252d; --muted:#667085; --bg:#f4f6f8; --surface:#fff; --green:#147d64; --red:#c62828; --amber:#a15c00; --border:#dfe3e8; }
                    * { box-sizing:border-box; }
                    body { margin:0; font-family:Inter,Arial,sans-serif; background:var(--bg); color:var(--dark); }
                    header { background:linear-gradient(115deg,#1d252d,#3b4650); color:#fff; padding:28px 5vw; display:flex; justify-content:space-between; align-items:flex-end; }
                    .brand { color:#ff8f4c; font-size:13px; font-weight:700; letter-spacing:2px; text-transform:uppercase; }
                    h1 { margin:8px 0 0; font-size:34px; }
                    .generated { font-size:12px; opacity:.8; }
                    nav { position:sticky; top:0; background:#fff; border-bottom:1px solid var(--border); padding:12px 5vw; display:flex; gap:10px; z-index:5; }
                    button { border:1px solid var(--border); background:#fff; padding:9px 14px; border-radius:7px; cursor:pointer; font-weight:600; }
                    button.active,button:hover { border-color:var(--orange); color:var(--orange); }
                    main { padding:28px 5vw 60px; max-width:1500px; margin:auto; }
                    .hero { display:grid; grid-template-columns:1.4fr repeat(3,1fr); gap:16px; margin-bottom:24px; }
                    .card { background:var(--surface); border:1px solid var(--border); border-radius:12px; padding:20px; box-shadow:0 4px 14px rgba(16,24,40,.05); }
                    .hero-main { border-top:5px solid var(--orange); }
                    .eyebrow { color:var(--muted); font-size:12px; text-transform:uppercase; letter-spacing:1px; font-weight:700; }
                    .big { font-size:40px; line-height:1; font-weight:800; margin-top:12px; }
                    .recommendation { margin-top:16px; font-weight:800; color:var(--green); }
                    .grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(290px,1fr)); gap:16px; }
                    .module { border-top:4px solid var(--border); }
                    .module.PASS { border-top-color:var(--green); }
                    .module.FAIL { border-top-color:var(--red); }
                    .module.NOT_RUN { border-top-color:var(--amber); }
                    .module-head { display:flex; justify-content:space-between; align-items:center; gap:10px; }
                    .badge { padding:5px 9px; border-radius:999px; font-size:12px; font-weight:800; }
                    .PASS .badge { color:#075b47; background:#e6f4ef; }
                    .FAIL .badge { color:#8f1d1d; background:#fdeaea; }
                    .NOT_RUN .badge { color:#765000; background:#fff4d6; }
                    dl { margin:16px 0 0; }
                    dl div { display:flex; justify-content:space-between; gap:12px; padding:7px 0; border-bottom:1px solid #eef0f2; }
                    dt { color:var(--muted); } dd { margin:0; text-align:right; font-weight:600; }
                    .links { display:flex; flex-wrap:wrap; gap:8px; margin-top:15px; }
                    .links a { color:var(--orange); text-decoration:none; font-size:13px; font-weight:700; } .links .detail-link{background:var(--orange);color:#fff;padding:8px 11px;border-radius:7px;}
                    .narrative { margin:24px 0; border-left:5px solid var(--orange); }
                    .engineering-only { display:none; }
                    body.engineering .engineering-only { display:block; }
                    body.engineering .executive-only { display:none; }
                    footer { color:var(--muted); text-align:center; padding:20px; font-size:12px; }
                    @media(max-width:900px){.hero{grid-template-columns:1fr 1fr}.hero-main{grid-column:1/-1}}
                    @media(max-width:600px){.hero{grid-template-columns:1fr}header{align-items:flex-start;flex-direction:column;gap:12px}}
                  </style>
                </head>
                <body>
                  <header>
                    <div><div class="brand">MAPAF Enterprise Quality Platform</div><h1>Enterprise Command Center</h1></div>
                    <div class="generated">Generated %s</div>
                  </header>
                  <nav>
                    <button id="executiveBtn" class="active">Executive View</button>
                    <button id="engineeringBtn">Engineering View</button>
                    <button onclick="document.getElementById('modules').scrollIntoView({behavior:'smooth'})">Module Health</button>
                    <button onclick="window.print()">Export / Print</button>
                  </nav>
                  <main>
                    <section class="hero executive-only">
                      <article class="card hero-main"><div class="eyebrow">%s · %s</div><div class="big">%d%%</div><p>Release readiness</p><div class="recommendation">%s</div></article>
                      <article class="card"><div class="eyebrow">Business Risk</div><div class="big">%s</div></article>
                      <article class="card"><div class="eyebrow">Overall Status</div><div class="big">%s</div></article>
                      <article class="card"><div class="eyebrow">Execution Success</div><div class="big">%.0f%%</div></article>
                    </section>
                    <section class="card narrative"><div class="eyebrow">AI-ready Executive Narrative</div><p>%s</p></section>
                    <section class="engineering-only hero">
                      <article class="card"><div class="eyebrow">Modules</div><div class="big">%d</div></article>
                      <article class="card"><div class="eyebrow">Total Duration</div><div class="big">%.1fs</div></article>
                      <article class="card"><div class="eyebrow">Environment</div><div class="big">%s</div></article>
                    </section>
                    <h2 id="modules">Business and Technical Health</h2>
                    <section class="grid">%s</section>
                  </main>
                  <footer>MAPAF v2.7.2 · Multi-Page Enterprise Quality Command Center</footer>
                  <script>
                    const executiveBtn=document.getElementById('executiveBtn');
                    const engineeringBtn=document.getElementById('engineeringBtn');
                    executiveBtn.onclick=()=>{document.body.classList.remove('engineering');executiveBtn.classList.add('active');engineeringBtn.classList.remove('active');};
                    engineeringBtn.onclick=()=>{document.body.classList.add('engineering');engineeringBtn.classList.add('active');executiveBtn.classList.remove('active');};
                  </script>
                </body>
                </html>
                """.formatted(
                escape(view.generatedAt()), escape(view.application()), escape(view.environment()), view.readinessScore(),
                escape(view.recommendation()), escape(view.risk()), escape(view.overallStatus()), view.overallSuccessRate(),
                escape(view.narrative()), view.modules().size(), view.totalDurationMillis() / 1000.0, escape(view.environment()), modules
        );
    }

    private String renderModule(EnterpriseModuleView module) {
        StringBuilder details = new StringBuilder();
        details.append(row("Scenario", module.scenario()));
        details.append(row("Success Rate", String.format("%.1f%%", module.successRate())));
        details.append(row("Passed / Failed", module.passed() + " / " + module.failed()));
        details.append(row("Duration", String.format("%.2f sec", module.durationMillis() / 1000.0)));
        for (Map.Entry<String, Object> entry : module.metrics().entrySet()) {
            details.append(row(entry.getKey(), entry.getValue()));
        }

        StringBuilder links = new StringBuilder();
        links.append("<a class=\"detail-link\" href=\"")
                .append(escape(module.key()))
                .append(".html\">Open detailed report</a>");
        module.links().forEach((name, url) -> links.append("<a href=\"").append(escape(url)).append("\" target=\"_blank\">").append(escape(name)).append("</a>"));

        return "<article class=\"card module " + escape(module.status()) + "\"><div class=\"module-head\"><div><div class=\"eyebrow\">RoomScan capability</div><h3>" + escape(module.name()) + "</h3></div><span class=\"badge\">" + escape(module.status()) + "</span></div><dl>" + details + "</dl><div class=\"links\">" + links + "</div></article>";
    }

    private String row(String key, Object value) {
        return "<div><dt>" + escape(key) + "</dt><dd>" + escape(value) + "</dd></div>";
    }

    private String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
