package core.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class K6SummaryParser {

    public double extractP95(Path summaryPath) {
        String json = read(summaryPath);
        return extractAfterMetric(
                json,
                "\"http_req_duration\"",
                "\"p(95)\""
        );
    }

    public double extractErrorRate(Path summaryPath) {
        String json = read(summaryPath);
        return extractAfterMetric(
                json,
                "\"http_req_failed\"",
                "\"value\""
        );
    }

    public double extractThroughput(Path summaryPath) {
        String json = read(summaryPath);
        return extractAfterMetric(
                json,
                "\"http_reqs\"",
                "\"rate\""
        );
    }

    private String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read k6 summary file",
                    e
            );
        }
    }

    private double extractAfterMetric(
            String json,
            String metricName,
            String fieldName
    ) {
        int metricIndex =
                json.indexOf(metricName);

        if (metricIndex < 0) {
            throw new IllegalArgumentException(
                    "Metric not found: " + metricName
            );
        }

        int fieldIndex =
                json.indexOf(fieldName, metricIndex);

        if (fieldIndex < 0) {
            throw new IllegalArgumentException(
                    "Field not found: " + fieldName
            );
        }

        int colonIndex =
                json.indexOf(":", fieldIndex);

        int startIndex =
                colonIndex + 1;

        while (startIndex < json.length()
                && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }

        int endIndex = startIndex;

        while (endIndex < json.length()) {
            char current =
                    json.charAt(endIndex);

            if (!(Character.isDigit(current)
                    || current == '.'
                    || current == '-')) {
                break;
            }

            endIndex++;
        }

        String rawValue =
                json.substring(startIndex, endIndex);

        return Double.parseDouble(rawValue);
    }
}