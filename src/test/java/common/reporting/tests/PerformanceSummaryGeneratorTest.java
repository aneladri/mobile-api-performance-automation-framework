package common.reporting.tests;

import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;
import performance.reporting.PerformanceSummaryGenerator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceSummaryGeneratorTest {

        @Test
        public void shouldGenerateSummaryFromK6AndJMeterResults()
                        throws Exception {

                Path temporaryDirectory = Files.createTempDirectory("mapaf-performance");

                Path k6Directory = temporaryDirectory.resolve("k6");
                Path jmeterDirectory = temporaryDirectory.resolve("jmeter");

                Files.createDirectories(k6Directory);
                Files.createDirectories(jmeterDirectory);

                String k6Summary = """
                                {
                                  "metrics": {
                                    "checks": {
                                      "passes": 8,
                                      "fails": 2,
                                      "value": 0.8
                                    },
                                    "http_reqs": {
                                      "count": 10,
                                      "rate": 1.0
                                    },
                                    "http_req_failed": {
                                      "passes": 8,
                                      "fails": 2,
                                      "value": 0.2
                                    }
                                  }
                                }
                                """;

                Files.writeString(
                                k6Directory.resolve("smoke-summary.json"),
                                k6Summary,
                                StandardCharsets.UTF_8);

                String jmeterResults = """
                                timeStamp,elapsed,label,responseCode,success
                                1000,100,health,200,true
                                1200,150,users,200,true
                                1500,200,users,500,false
                                """;

                Files.writeString(
                                jmeterDirectory.resolve("smoke.jtl"),
                                jmeterResults,
                                StandardCharsets.UTF_8);

                ExecutionSummary summary = PerformanceSummaryGenerator.generate(
                                k6Directory,
                                jmeterDirectory);

                Assert.assertEquals(
                                summary.getModule(),
                                "Performance");

                Assert.assertEquals(
                                summary.getStatus(),
                                ExecutionStatus.FAIL);

                Assert.assertEquals(
                                summary.getMetrics().getTotal(),
                                13);

                Assert.assertEquals(
                                summary.getMetrics().getPassed(),
                                10);

                Assert.assertEquals(
                                summary.getMetrics().getFailed(),
                                3);

                Assert.assertEquals(
                                summary.getDetails().get("k6Files"),
                                1);

                Assert.assertEquals(
                                summary.getDetails().get("jmeterFiles"),
                                1);
        }

        @Test
        public void shouldReturnNotRunWhenNoResultsExist()
                        throws Exception {

                Path temporaryDirectory = Files.createTempDirectory("mapaf-no-performance");

                ExecutionSummary summary = PerformanceSummaryGenerator.generate(
                                temporaryDirectory.resolve("k6"),
                                temporaryDirectory.resolve("jmeter"));

                Assert.assertEquals(
                                summary.getStatus(),
                                ExecutionStatus.NOT_RUN);

                Assert.assertEquals(
                                summary.getMetrics().getTotal(),
                                0);
        }
}
