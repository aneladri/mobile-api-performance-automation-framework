package performance.reporting;

import java.nio.file.Path;

public final class PerformanceSummaryCli {

    private PerformanceSummaryCli() {
    }

    public static void main(String[] args) throws Exception {
        Path projectDirectory = Path.of(
                System.getProperty("user.dir")
        );

        Path k6Results = projectDirectory.resolve(
                "performance/results/k6"
        );

        Path jmeterResults = projectDirectory.resolve(
                "performance/jmeter/results"
        );

        Path outputFile = projectDirectory.resolve(
                "performance/reports/summary.json"
        );

        Path generatedFile =
                PerformanceSummaryGenerator.generateAndWrite(
                        k6Results,
                        jmeterResults,
                        outputFile
                );

        System.out.println(
                "MAPAF performance summary: " + generatedFile
        );
    }
}
