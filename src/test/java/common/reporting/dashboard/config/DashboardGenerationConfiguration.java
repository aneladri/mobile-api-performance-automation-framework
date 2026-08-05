package common.reporting.dashboard.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DashboardGenerationConfiguration {

    private final List<Path> summaryPaths;
    private final Path outputDirectory;
    private final DashboardConfiguration dashboardConfiguration;

    private DashboardGenerationConfiguration(
            Builder builder
    ) {
        this.summaryPaths =
                new ArrayList<>(builder.summaryPaths);

        this.outputDirectory =
                builder.outputDirectory;

        this.dashboardConfiguration =
                builder.dashboardConfiguration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DashboardGenerationConfiguration
    defaultConfiguration() {

        return builder()
                .addSummaryPath(
                        Paths.get(
                                "api",
                                "reports",
                                "summary.json"
                        )
                )
                .addSummaryPath(
                        Paths.get(
                                "web",
                                "reports",
                                "summary.json"
                        )
                )
                .addSummaryPath(
                        Paths.get(
                                "performance",
                                "reports",
                                "summary.json"
                        )
                )
                .outputDirectory(
                        Paths.get(
                                "dashboard",
                                "reports"
                        )
                )
                .dashboardConfiguration(
                        DashboardConfiguration
                                .defaultConfiguration()
                )
                .build();
    }

    public List<Path> getSummaryPaths() {
        return new ArrayList<>(summaryPaths);
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public DashboardConfiguration getDashboardConfiguration() {
        return dashboardConfiguration;
    }

    public static final class Builder {

        private final List<Path> summaryPaths =
                new ArrayList<>();

        private Path outputDirectory =
                Paths.get(
                        "dashboard",
                        "reports"
                );

        private DashboardConfiguration dashboardConfiguration =
                DashboardConfiguration.defaultConfiguration();

        private Builder() {
        }

        public Builder addSummaryPath(
                Path summaryPath
        ) {
            if (summaryPath == null) {
                throw new IllegalArgumentException(
                        "Summary path must not be null"
                );
            }

            summaryPaths.add(summaryPath);

            return this;
        }

        public Builder summaryPaths(
                List<Path> summaryPaths
        ) {
            if (summaryPaths == null) {
                throw new IllegalArgumentException(
                        "Summary paths must not be null"
                );
            }

            boolean containsNull =
                    summaryPaths.stream()
                            .anyMatch(path -> path == null);

            if (containsNull) {
                throw new IllegalArgumentException(
                        "Summary paths must not contain null"
                );
            }

            this.summaryPaths.clear();
            this.summaryPaths.addAll(summaryPaths);

            return this;
        }

        public Builder outputDirectory(
                Path outputDirectory
        ) {
            if (outputDirectory == null) {
                throw new IllegalArgumentException(
                        "Dashboard output directory must not be null"
                );
            }

            this.outputDirectory = outputDirectory;

            return this;
        }

        public Builder dashboardConfiguration(
                DashboardConfiguration dashboardConfiguration
        ) {
            if (dashboardConfiguration == null) {
                throw new IllegalArgumentException(
                        "Dashboard configuration must not be null"
                );
            }

            this.dashboardConfiguration =
                    dashboardConfiguration;

            return this;
        }

        public DashboardGenerationConfiguration build() {
            return new DashboardGenerationConfiguration(
                    this
            );
        }
    }
}