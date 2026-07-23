package common.reporting.dashboard.pipeline;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.config.DashboardGenerationConfiguration;
import common.reporting.dashboard.generator.DashboardGenerator;
import common.reporting.dashboard.generator.DashboardGeneratorCli;
import common.reporting.dashboard.publisher.DashboardPublisher;
import common.reporting.model.ExecutionSummary;

import java.nio.file.Path;
import java.util.List;

public class DashboardGenerationPipeline {

    private final DashboardGenerator dashboardGenerator;
    private final DashboardPublisher dashboardPublisher;

    public DashboardGenerationPipeline() {
        this(
                new DashboardGenerator(),
                new DashboardPublisher()
        );
    }

    DashboardGenerationPipeline(
            DashboardGenerator dashboardGenerator,
            DashboardPublisher dashboardPublisher
    ) {
        if (dashboardGenerator == null) {
            throw new IllegalArgumentException(
                    "Dashboard generator must not be null"
            );
        }

        if (dashboardPublisher == null) {
            throw new IllegalArgumentException(
                    "Dashboard publisher must not be null"
            );
        }

        this.dashboardGenerator = dashboardGenerator;
        this.dashboardPublisher = dashboardPublisher;
    }

    public Path execute(
            DashboardGenerationConfiguration configuration,
            Path publicationDirectory
    ) throws Exception {

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Dashboard generation configuration must not be null"
            );
        }

        if (publicationDirectory == null) {
            throw new IllegalArgumentException(
                    "Dashboard publication directory must not be null"
            );
        }

        List<Path> summaryPaths =
                configuration.getSummaryPaths();

        List<ExecutionSummary> summaries =
                DashboardGeneratorCli.readAvailableSummaries(
                        summaryPaths.toArray(Path[]::new)
                );

        DashboardSummary dashboardSummary =
                new DashboardAggregator().aggregate(
                        summaries
                );

        Path generatedDashboard =
                dashboardGenerator.generate(
                        dashboardSummary,
                        configuration
                );

        return dashboardPublisher.publish(
                generatedDashboard,
                publicationDirectory
        );
    }
}
