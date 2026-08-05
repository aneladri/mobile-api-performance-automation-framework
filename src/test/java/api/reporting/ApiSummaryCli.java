package api.reporting;

import java.nio.file.Path;

public final class ApiSummaryCli {

    private ApiSummaryCli() {
    }

    public static void main(String[] args) throws Exception {
        Path projectDirectory = Path.of(
                System.getProperty("user.dir")
        );

        Path testResultsDirectory = projectDirectory.resolve(
                "build/test-results/apiTest"
        );

        Path outputFile = projectDirectory.resolve(
                "api/reports/summary.json"
        );

        Path generatedFile =
                ApiSummaryGenerator.generateAndWrite(
                        testResultsDirectory,
                        outputFile
                );

        System.out.println(
                "MAPAF API summary: " + generatedFile
        );
    }
}
