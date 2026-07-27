package performance.reporting;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class JMeterSummaryParser {

    public PerformanceParseResult parseDirectory(Path directory)
            throws IOException {

        PerformanceParseResult result = new PerformanceParseResult();

        if (directory == null || !Files.isDirectory(directory)) {
            return result;
        }

        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> jtlFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName()
                            .toString()
                            .toLowerCase(Locale.ROOT)
                            .endsWith(".jtl"))
                    .toList();

            for (Path file : jtlFiles) {
                parseFile(file, result);
            }
        }

        return result;
    }

    private void parseFile(
            Path file,
            PerformanceParseResult result
    ) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(
                file,
                StandardCharsets.UTF_8
        )) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isBlank()) {
                return;
            }

            List<String> headers = parseCsvLine(headerLine);

            int successIndex = indexOf(headers, "success");
            int timestampIndex = indexOf(headers, "timeStamp");
            int elapsedIndex = indexOf(headers, "elapsed");

            if (successIndex < 0) {
                throw new IOException(
                        "JMeter result does not contain a success column: "
                                + file.toAbsolutePath()
                );
            }

            int total = 0;
            int passed = 0;
            int failed = 0;

            long minimumTimestamp = Long.MAX_VALUE;
            long maximumEndTimestamp = 0;

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);

                if (successIndex >= values.size()) {
                    continue;
                }

                boolean success = Boolean.parseBoolean(
                        values.get(successIndex).trim()
                );

                total++;

                if (success) {
                    passed++;
                } else {
                    failed++;
                }

                if (timestampIndex >= 0
                        && elapsedIndex >= 0
                        && timestampIndex < values.size()
                        && elapsedIndex < values.size()) {

                    long timestamp = parseLong(
                            values.get(timestampIndex)
                    );

                    long elapsed = parseLong(
                            values.get(elapsedIndex)
                    );

                    if (timestamp > 0) {
                        minimumTimestamp = Math.min(
                                minimumTimestamp,
                                timestamp
                        );

                        maximumEndTimestamp = Math.max(
                                maximumEndTimestamp,
                                timestamp + elapsed
                        );
                    }
                }
            }

            if (total == 0) {
                return;
            }

            long durationSeconds = 0;

            if (minimumTimestamp != Long.MAX_VALUE
                    && maximumEndTimestamp >= minimumTimestamp) {

                durationSeconds = Math.max(
                        1,
                        Math.round(
                                (maximumEndTimestamp - minimumTimestamp)
                                        / 1000.0
                        )
                );
            }

            result.add(
                    total,
                    passed,
                    failed,
                    durationSeconds,
                    total
            );
        }
    }

    private int indexOf(
            List<String> headers,
            String expected
    ) {
        for (int index = 0; index < headers.size(); index++) {
            if (expected.equalsIgnoreCase(
                    headers.get(index).trim()
            )) {
                return index;
            }
        }

        return -1;
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);

            if (current == '"') {
                if (quoted
                        && index + 1 < line.length()
                        && line.charAt(index + 1) == '"') {

                    currentValue.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(current);
            }
        }

        values.add(currentValue.toString());

        return values;
    }
}
