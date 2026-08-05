package common.reporting.dashboard.generator;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.io.SummaryReader;
import common.reporting.model.ExecutionSummary;
import common.reporting.dashboard.config.DashboardGenerationConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class DashboardGeneratorCli {

        private DashboardGeneratorCli() {
        }

        public static void main(String[] args) {
                try {
                        DashboardGenerationConfiguration configuration = createConfiguration(args);

                        List<ExecutionSummary> summaries = readAvailableSummaries(
                                        configuration
                                                        .getSummaryPaths()
                                                        .toArray(Path[]::new));

                        DashboardSummary dashboardSummary = new DashboardAggregator().aggregate(
                                        summaries);

                        Path generatedFile = new DashboardGenerator().generate(
                                        dashboardSummary,
                                        configuration);

                        System.out.println(
                                        "Dashboard generated successfully: "
                                                        + generatedFile);
                } catch (Exception exception) {
                        System.err.println(
                                        "Dashboard generation failed: "
                                                        + exception.getMessage());

                        exception.printStackTrace(System.err);

                        System.exit(1);
                }
        }

        public static List<ExecutionSummary> readAvailableSummaries(
                        Path... summaryPaths) throws Exception {
                List<ExecutionSummary> summaries = new ArrayList<>();

                if (summaryPaths == null) {
                        return summaries;
                }

                for (Path summaryPath : summaryPaths) {
                        if (summaryPath == null
                                        || !Files.isRegularFile(summaryPath)) {

                                printSkippedFile(summaryPath);
                                continue;
                        }

                        ExecutionSummary summary = SummaryReader.read(summaryPath);

                        summaries.add(summary);

                        System.out.println(
                                        "Loaded summary: "
                                                        + summaryPath.toAbsolutePath());
                }

                return summaries;
        }

        private static void printSkippedFile(
                        Path summaryPath) {
                String value = summaryPath == null
                                ? "null"
                                : summaryPath.toAbsolutePath().toString();

                System.out.println(
                                "Summary file not found; skipping: "
                                                + value);
        }

        public static DashboardGenerationConfiguration createConfiguration(
                        String[] args) {
                if (args == null || args.length == 0) {
                        return DashboardGenerationConfiguration
                                        .defaultConfiguration();
                }

                DashboardGenerationConfiguration.Builder builder = DashboardGenerationConfiguration.builder();

                for (String argument : args) {
                        if (argument != null && !argument.isBlank()) {
                                builder.addSummaryPath(
                                                Paths.get(argument));
                        }
                }

                return builder.build();
        }
}
