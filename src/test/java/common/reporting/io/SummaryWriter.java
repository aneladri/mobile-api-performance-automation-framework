package common.reporting.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import common.reporting.model.ExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class SummaryWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private SummaryWriter() {
        // Utility class
    }

    public static Path write(
            ExecutionSummary summary,
            Path outputFile
    ) throws IOException {

        validate(summary);
        Objects.requireNonNull(outputFile, "Output file must not be null");

        Path parent = outputFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        OBJECT_MAPPER.writeValue(outputFile.toFile(), summary);

        return outputFile.toAbsolutePath();
    }

    private static void validate(ExecutionSummary summary) {
        Objects.requireNonNull(summary, "Execution summary must not be null");

        if (summary.getModule() == null || summary.getModule().isBlank()) {
            throw new IllegalArgumentException(
                    "Execution summary module must not be blank"
            );
        }

        if (summary.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Execution summary status must not be null"
            );
        }

        if (summary.getMetrics() == null) {
            throw new IllegalArgumentException(
                    "Execution summary metrics must not be null"
            );
        }

        if (summary.getEnvironment() == null) {
            throw new IllegalArgumentException(
                    "Execution summary environment must not be null"
            );
        }
    }
}
