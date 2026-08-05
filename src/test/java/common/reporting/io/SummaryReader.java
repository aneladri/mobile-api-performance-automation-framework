package common.reporting.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.reporting.model.ExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class SummaryReader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false
            );

    private SummaryReader() {
        // Utility class
    }

    public static ExecutionSummary read(Path inputFile) throws IOException {
        Objects.requireNonNull(inputFile, "Input file must not be null");

        if (!Files.exists(inputFile)) {
            throw new IOException(
                    "Execution summary file does not exist: "
                            + inputFile.toAbsolutePath()
            );
        }

        if (!Files.isRegularFile(inputFile)) {
            throw new IOException(
                    "Execution summary path is not a file: "
                            + inputFile.toAbsolutePath()
            );
        }

        ExecutionSummary summary = OBJECT_MAPPER.readValue(
                inputFile.toFile(),
                ExecutionSummary.class
        );

        validate(summary, inputFile);

        return summary;
    }

    private static void validate(
            ExecutionSummary summary,
            Path inputFile
    ) throws IOException {

        if (summary == null) {
            throw invalidFile(inputFile, "summary is empty");
        }

        if (summary.getModule() == null || summary.getModule().isBlank()) {
            throw invalidFile(inputFile, "module is missing");
        }

        if (summary.getStatus() == null) {
            throw invalidFile(inputFile, "status is missing");
        }

        if (summary.getMetrics() == null) {
            throw invalidFile(inputFile, "metrics are missing");
        }

        if (summary.getEnvironment() == null) {
            throw invalidFile(inputFile, "environment is missing");
        }
    }

    private static IOException invalidFile(
            Path inputFile,
            String reason
    ) {
        return new IOException(
                "Invalid execution summary "
                        + inputFile.toAbsolutePath()
                        + ": "
                        + reason
        );
    }
}
