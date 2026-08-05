package web.reporting;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class WebSummaryCli {

    private static final Path DEFAULT_RESULTS_DIRECTORY =
            Paths.get(
                    "build",
                    "test-results",
                    "webTest"
            );

    private static final Path DEFAULT_OUTPUT_FILE =
            Paths.get(
                    "web",
                    "reports",
                    "summary.json"
            );

    private WebSummaryCli() {
    }

    public static void main(String[] args) {
        try {
            Path resultsDirectory =
                    resolvePath(
                            args,
                            0,
                            DEFAULT_RESULTS_DIRECTORY
                    );

            Path outputFile =
                    resolvePath(
                            args,
                            1,
                            DEFAULT_OUTPUT_FILE
                    );

            Path generatedFile =
                    WebSummaryGenerator
                            .generateAndWrite(
                                    resultsDirectory,
                                    outputFile
                            );

            System.out.println(
                    "Web summary generated successfully: "
                            + generatedFile
            );
        } catch (Exception exception) {
            System.err.println(
                    "Web summary generation failed: "
                            + exception.getMessage()
            );

            exception.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static Path resolvePath(
            String[] args,
            int index,
            Path defaultPath
    ) {
        if (args == null
                || args.length <= index
                || args[index] == null
                || args[index].isBlank()) {

            return defaultPath;
        }

        return Paths.get(args[index]);
    }
}
