package performance.jmeter;

import performance.models.PerformanceResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class JMeterResultParser {

    public PerformanceResult parse(
            Path resultFile
    ) {

        try {

            List<String> lines =
                    Files.readAllLines(resultFile);

            if (lines.size() <= 1) {
                throw new IllegalArgumentException(
                        "JMeter result file is empty."
                );
            }

            DoubleSummaryStatistics statistics =
                    new DoubleSummaryStatistics();

            long failures = 0;

            for (int i = 1; i < lines.size(); i++) {

                String[] columns =
                        lines.get(i).split(",");

                /*
                 * JMeter CSV format
                 *
                 * timeStamp,
                 * elapsed,
                 * label,
                 * responseCode,
                 * responseMessage,
                 * threadName,
                 * dataType,
                 * success,
                 * failureMessage,
                 * bytes,
                 * sentBytes,
                 * grpThreads,
                 * allThreads,
                 * URL,
                 * Latency,
                 * IdleTime,
                 * Connect
                 */

                double elapsed =
                        Double.parseDouble(columns[1]);

                boolean success =
                        Boolean.parseBoolean(columns[7]);

                statistics.accept(elapsed);

                if (!success) {
                    failures++;
                }
            }

            long samples =
                    statistics.getCount();

            double average =
                    statistics.getAverage();

            double errorRate =
                    samples == 0
                            ? 0
                            : ((double) failures / samples);

            return new PerformanceResult(
                    "JMeter",
                    resultFile.getFileName().toString(),
                    average,
                    average,
                    average,
                    errorRate,
                    samples,
                    samples,
                    Instant.now()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to parse JMeter results.",
                    e
            );
        }
    }
}
