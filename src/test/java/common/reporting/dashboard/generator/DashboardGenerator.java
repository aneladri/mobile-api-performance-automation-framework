package common.reporting.dashboard.generator;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.builder.DashboardBuilder;
import common.reporting.dashboard.builder.DashboardPage;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.renderer.DashboardHtmlRenderer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardGenerator {

    private final DashboardBuilder dashboardBuilder;
    private final DashboardHtmlRenderer htmlRenderer;

    public DashboardGenerator() {
        this(
                new DashboardBuilder(),
                new DashboardHtmlRenderer()
        );
    }

    DashboardGenerator(
            DashboardBuilder dashboardBuilder,
            DashboardHtmlRenderer htmlRenderer
    ) {

        if (dashboardBuilder == null) {
            throw new IllegalArgumentException(
                    "Dashboard builder must not be null"
            );
        }

        if (htmlRenderer == null) {
            throw new IllegalArgumentException(
                    "Dashboard HTML renderer must not be null"
            );
        }

        this.dashboardBuilder = dashboardBuilder;
        this.htmlRenderer = htmlRenderer;
    }

    public Path generate(
            DashboardSummary summary
    ) throws IOException {

        return generate(
                summary,
                DashboardConfiguration.defaultConfiguration(),
                Paths.get(
                        "dashboard",
                        "reports"
                )
        );
    }

    public Path generate(
            DashboardSummary summary,
            DashboardConfiguration configuration,
            Path outputDirectory
    ) throws IOException {

        if (summary == null) {
            throw new IllegalArgumentException(
                    "Dashboard summary must not be null"
            );
        }

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Dashboard configuration must not be null"
            );
        }

        if (outputDirectory == null) {
            throw new IllegalArgumentException(
                    "Dashboard output directory must not be null"
            );
        }

        DashboardPage page =
                dashboardBuilder.build(
                        summary,
                        configuration
                );

        Path outputFile =
                outputDirectory.resolve(
                        configuration.getReportFileName()
                );

        return htmlRenderer.render(
                page,
                outputFile
        );
    }
}
