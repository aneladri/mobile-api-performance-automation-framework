package common.reporting.dashboard.renderer;

import common.reporting.dashboard.builder.DashboardPage;
import common.reporting.dashboard.config.DashboardTheme;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DashboardHtmlRenderer {

    public Path render(
            DashboardPage page,
            Path outputFile
    ) throws IOException {

        validate(page, outputFile);

        Path parent = outputFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
                outputFile,
                renderToString(page),
                StandardCharsets.UTF_8
        );

        return outputFile.toAbsolutePath();
    }

    public String renderToString(DashboardPage page) {
        if (page == null) {
            throw new IllegalArgumentException(
                    "Dashboard page must not be null"
            );
        }

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" ")
                .append("content=\"width=device-width, initial-scale=1.0\">\n");

        html.append("  <title>")
                .append(HtmlEscaper.escape(
                        page.getConfiguration().getTitle()
                ))
                .append("</title>\n");

        html.append("  <style>\n");
        html.append(buildCss(
                page.getConfiguration().getTheme()
        ));
        html.append("  </style>\n");
        html.append("</head>\n");

        html.append("<body>\n");
        html.append(renderHeader(page));
        html.append(renderNavigation(page));

        html.append("<main class=\"dashboard-container\">\n");

        for (DashboardSection section
                : page.getVisibleSections()) {

            html.append(renderSection(section));
        }

        html.append("</main>\n");
        html.append(renderFooter(page));
        html.append(renderJavascript());
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String renderHeader(DashboardPage page) {
        return """
                <header class="dashboard-header">
                  <div>
                    <div class="product-name">%s</div>
                    <h1>%s</h1>
                    <div class="dashboard-version">Version %s</div>
                  </div>
                  <div class="generated-time">
                    Generated: %s
                  </div>
                </header>
                """.formatted(
                HtmlEscaper.escape(
                        page.getConfiguration().getProductName()
                ),
                HtmlEscaper.escape(
                        page.getConfiguration().getTitle()
                ),
                HtmlEscaper.escape(
                        page.getConfiguration().getVersion()
                ),
                HtmlEscaper.escape(page.getGeneratedAt())
        );
    }

    private String renderNavigation(DashboardPage page) {
        StringBuilder navigation = new StringBuilder();

        navigation.append("<nav class=\"dashboard-nav\">\n");

        for (DashboardSection section
                : page.getVisibleSections()) {

            navigation.append("  <button class=\"nav-button\" ")
                    .append("data-target=\"")
                    .append(HtmlEscaper.escape(section.getId()))
                    .append("\">")
                    .append(HtmlEscaper.escape(section.getTitle()))
                    .append("</button>\n");
        }

        navigation.append("</nav>\n");

        return navigation.toString();
    }

    private String renderSection(DashboardSection section) {
        StringBuilder html = new StringBuilder();

        html.append("<section class=\"dashboard-section\" id=\"")
                .append(HtmlEscaper.escape(section.getId()))
                .append("\">\n");

        html.append("  <h2>")
                .append(HtmlEscaper.escape(section.getTitle()))
                .append("</h2>\n");

        html.append("  <div class=\"widget-grid\">\n");

        for (DashboardWidget widget
                : section.getVisibleWidgets()) {

            html.append(renderWidget(widget));
        }

        html.append("  </div>\n");
        html.append("</section>\n");

        return html.toString();
    }

    private String renderWidget(DashboardWidget widget) {
        StringBuilder html = new StringBuilder();

        html.append("<article class=\"dashboard-widget ")
                .append(statusClass(widget.getStatus()))
                .append(" widget-")
                .append(widget.getType().name().toLowerCase())
                .append("\">\n");

        html.append("  <div class=\"widget-header\">\n");
        html.append("    <h3>")
                .append(HtmlEscaper.escape(widget.getTitle()))
                .append("</h3>\n");

        if (widget.getSubtitle() != null
                && !widget.getSubtitle().isBlank()) {

            html.append("    <p class=\"widget-subtitle\">")
                    .append(HtmlEscaper.escape(
                            widget.getSubtitle()
                    ))
                    .append("</p>\n");
        }

        html.append("  </div>\n");

        if (widget.getType() == WidgetType.KPI
                || widget.getType() == WidgetType.STATUS) {

            html.append(renderPrimaryValue(widget));
        } else {
            html.append(renderDataTable(widget));
        }

        html.append(renderLinks(widget));
        html.append("</article>\n");

        return html.toString();
    }

    private String renderPrimaryValue(DashboardWidget widget) {
        Object value = widget.getData().get("value");
        Object unit = widget.getData().get("unit");

        return """
                  <div class="primary-value">
                    <span>%s</span>
                    <small>%s</small>
                  </div>
                """.formatted(
                HtmlEscaper.escape(value),
                HtmlEscaper.escape(unit)
        );
    }

    private String renderDataTable(DashboardWidget widget) {
        if (widget.getData().isEmpty()) {
            return "<p class=\"empty-state\">No data available</p>\n";
        }

        StringBuilder html = new StringBuilder();

        html.append("<dl class=\"widget-data\">\n");

        for (Map.Entry<String, Object> entry
                : widget.getData().entrySet()) {

            html.append("  <div>\n");
            html.append("    <dt>")
                    .append(formatLabel(entry.getKey()))
                    .append("</dt>\n");
            html.append("    <dd>")
                    .append(HtmlEscaper.escape(entry.getValue()))
                    .append("</dd>\n");
            html.append("  </div>\n");
        }

        html.append("</dl>\n");

        return html.toString();
    }

    private String renderLinks(DashboardWidget widget) {
        if (widget.getLinks().isEmpty()) {
            return "";
        }

        StringBuilder html = new StringBuilder();

        html.append("<div class=\"widget-links\">\n");

        for (Map.Entry<String, String> link
                : widget.getLinks().entrySet()) {

            html.append("  <a href=\"")
                    .append(HtmlEscaper.escape(link.getValue()))
                    .append("\" target=\"_blank\" ")
                    .append("rel=\"noopener noreferrer\">")
                    .append(HtmlEscaper.escape(link.getKey()))
                    .append("</a>\n");
        }

        html.append("</div>\n");

        return html.toString();
    }

    private String renderFooter(DashboardPage page) {
        return """
                <footer class="dashboard-footer">
                  <span>%s</span>
                  <span>MAPAF Unified Quality Dashboard</span>
                </footer>
                """.formatted(
                HtmlEscaper.escape(
                        page.getConfiguration().getProductName()
                )
        );
    }

    private String renderJavascript() {
        return """
                <script>
                  document.querySelectorAll('.nav-button').forEach(button => {
                    button.addEventListener('click', () => {
                      const target = document.getElementById(
                        button.dataset.target
                      );

                      if (target) {
                        target.scrollIntoView({
                          behavior: 'smooth',
                          block: 'start'
                        });
                      }
                    });
                  });
                </script>
                """;
    }

    private String buildCss(DashboardTheme theme) {
        String colorScheme = theme == DashboardTheme.DARK
                ? "dark"
                : "light";

        return """
                :root {
                  color-scheme: %s;
                  --background: #f4f5f7;
                  --surface: #ffffff;
                  --surface-alt: #f8f9fa;
                  --text: #1f2933;
                  --muted: #667085;
                  --border: #dfe3e8;
                  --primary: #d04a02;
                  --success: #1f7a4d;
                  --failure: #c62828;
                  --warning: #b26a00;
                  --information: #175cd3;
                  --unavailable: #667085;
                  --shadow: 0 8px 24px rgba(16, 24, 40, 0.08);
                }

                * {
                  box-sizing: border-box;
                }

                body {
                  margin: 0;
                  font-family: Arial, Helvetica, sans-serif;
                  background: var(--background);
                  color: var(--text);
                }

                .dashboard-header {
                  display: flex;
                  justify-content: space-between;
                  align-items: flex-start;
                  padding: 28px 40px;
                  background: var(--surface);
                  border-bottom: 4px solid var(--primary);
                }

                .product-name {
                  color: var(--primary);
                  font-weight: 700;
                  letter-spacing: 0.08em;
                  text-transform: uppercase;
                }

                h1 {
                  margin: 6px 0;
                  font-size: 30px;
                }

                .dashboard-version,
                .generated-time,
                .widget-subtitle {
                  color: var(--muted);
                  font-size: 13px;
                }

                .dashboard-nav {
                  position: sticky;
                  top: 0;
                  z-index: 10;
                  display: flex;
                  gap: 8px;
                  padding: 14px 40px;
                  background: var(--surface);
                  border-bottom: 1px solid var(--border);
                  overflow-x: auto;
                }

                .nav-button {
                  padding: 9px 15px;
                  border: 1px solid var(--border);
                  border-radius: 20px;
                  background: var(--surface-alt);
                  color: var(--text);
                  cursor: pointer;
                }

                .nav-button:hover {
                  border-color: var(--primary);
                  color: var(--primary);
                }

                .dashboard-container {
                  max-width: 1440px;
                  margin: 0 auto;
                  padding: 24px 40px 48px;
                }

                .dashboard-section {
                  scroll-margin-top: 80px;
                  margin-bottom: 34px;
                }

                .dashboard-section h2 {
                  margin-bottom: 16px;
                  font-size: 22px;
                }

                .widget-grid {
                  display: grid;
                  grid-template-columns:
                    repeat(auto-fit, minmax(240px, 1fr));
                  gap: 16px;
                }

                .dashboard-widget {
                  padding: 20px;
                  background: var(--surface);
                  border: 1px solid var(--border);
                  border-top: 4px solid var(--information);
                  border-radius: 10px;
                  box-shadow: var(--shadow);
                }

                .widget-header h3 {
                  margin: 0 0 8px;
                  font-size: 17px;
                }

                .status-success {
                  border-top-color: var(--success);
                }

                .status-failure {
                  border-top-color: var(--failure);
                }

                .status-warning {
                  border-top-color: var(--warning);
                }

                .status-information {
                  border-top-color: var(--information);
                }

                .status-not-available {
                  border-top-color: var(--unavailable);
                }

                .primary-value {
                  display: flex;
                  align-items: baseline;
                  gap: 8px;
                  margin-top: 20px;
                }

                .primary-value span {
                  font-size: 32px;
                  font-weight: 700;
                }

                .primary-value small {
                  color: var(--muted);
                }

                .widget-data {
                  margin: 16px 0 0;
                }

                .widget-data div {
                  display: flex;
                  justify-content: space-between;
                  gap: 16px;
                  padding: 9px 0;
                  border-bottom: 1px solid var(--border);
                }

                .widget-data dt {
                  color: var(--muted);
                }

                .widget-data dd {
                  margin: 0;
                  font-weight: 600;
                  text-align: right;
                  overflow-wrap: anywhere;
                }

                .widget-links {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 8px;
                  margin-top: 18px;
                }

                .widget-links a {
                  padding: 8px 12px;
                  border-radius: 6px;
                  background: var(--surface-alt);
                  color: var(--primary);
                  text-decoration: none;
                  font-size: 13px;
                  font-weight: 600;
                }

                .widget-links a:hover {
                  text-decoration: underline;
                }

                .empty-state {
                  color: var(--muted);
                }

                .dashboard-footer {
                  display: flex;
                  justify-content: space-between;
                  padding: 20px 40px;
                  background: var(--surface);
                  border-top: 1px solid var(--border);
                  color: var(--muted);
                  font-size: 13px;
                }

                @media (max-width: 700px) {
                  .dashboard-header,
                  .dashboard-footer {
                    flex-direction: column;
                    gap: 10px;
                  }

                  .dashboard-header,
                  .dashboard-nav,
                  .dashboard-container,
                  .dashboard-footer {
                    padding-left: 20px;
                    padding-right: 20px;
                  }
                }
                """.formatted(colorScheme);
    }

    private String statusClass(WidgetStatus status) {
        if (status == null) {
            return "status-not-available";
        }

        return switch (status) {
            case SUCCESS -> "status-success";
            case FAILURE -> "status-failure";
            case WARNING -> "status-warning";
            case INFORMATION -> "status-information";
            case NOT_AVAILABLE -> "status-not-available";
        };
    }

    private String formatLabel(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return HtmlEscaper.escape(
                value.replaceAll(
                        "([a-z])([A-Z])",
                        "$1 $2"
                )
                .replace("-", " ")
                .replace("_", " ")
        );
    }

    private void validate(
            DashboardPage page,
            Path outputFile
    ) {
        if (page == null) {
            throw new IllegalArgumentException(
                    "Dashboard page must not be null"
            );
        }

        if (page.getConfiguration() == null) {
            throw new IllegalArgumentException(
                    "Dashboard configuration must not be null"
            );
        }

        if (outputFile == null) {
            throw new IllegalArgumentException(
                    "Dashboard output file must not be null"
            );
        }
    }
}
