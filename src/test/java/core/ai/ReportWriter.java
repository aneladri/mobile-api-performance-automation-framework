package core.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ReportWriter {

    private ReportWriter() {
    }

    public static Path writeReport(
            String report,
            String fileName
    ) {
        try {
            Path outputDir =
                    Path.of("reports", "ai", "generated");

            Files.createDirectories(outputDir);

            Path reportPath =
                    outputDir.resolve(fileName);

            Files.writeString(reportPath, report);

            return reportPath;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to write AI report",
                    e
            );
        }
    }
}
