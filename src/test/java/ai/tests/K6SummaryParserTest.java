package ai.tests;

import core.ai.K6SummaryParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;

public class K6SummaryParserTest {

    @Test
    public void verifyK6SummaryMetricsAreParsed() {

        K6SummaryParser parser =
                new K6SummaryParser();

        Path summaryPath =
                Path.of("performance", "results", "smoke-summary.json");

        double p95 =
                parser.extractP95(summaryPath);

        double errorRate =
                parser.extractErrorRate(summaryPath);

        double throughput =
                parser.extractThroughput(summaryPath);

        Assert.assertTrue(p95 > 0);
        Assert.assertTrue(errorRate >= 0);
        Assert.assertTrue(throughput > 0);
    }
}
