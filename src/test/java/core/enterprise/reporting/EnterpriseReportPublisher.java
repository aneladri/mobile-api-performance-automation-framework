package core.enterprise.reporting;

import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionSummary;

import java.io.IOException;
import java.nio.file.Path;

public final class EnterpriseReportPublisher {
    private EnterpriseReportPublisher() {}
    public static Path publish(ExecutionSummary summary, Path outputFile) {
        try {
            return SummaryWriter.write(summary, outputFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to publish enterprise execution summary", exception);
        }
    }
}
