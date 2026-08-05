package common.reporting.tests;

import api.reporting.ApiSummaryGenerator;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ApiSummaryGeneratorTest {

    @Test
    public void shouldGenerateApiSummaryFromGradleXml()
            throws Exception {

        Path directory =
                Files.createTempDirectory("mapaf-api-results");

        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite
                    name="api.tests"
                    tests="5"
                    skipped="1"
                    failures="1"
                    errors="0"
                    time="2.5">
                </testsuite>
                """;

        Files.writeString(
                directory.resolve("TEST-api.tests.xml"),
                xml,
                StandardCharsets.UTF_8
        );

        ExecutionSummary summary =
                ApiSummaryGenerator.generate(directory);

        Assert.assertEquals(summary.getModule(), "API");
        Assert.assertEquals(
                summary.getStatus(),
                ExecutionStatus.FAIL
        );

        Assert.assertEquals(
                summary.getMetrics().getTotal(),
                5
        );

        Assert.assertEquals(
                summary.getMetrics().getPassed(),
                3
        );

        Assert.assertEquals(
                summary.getMetrics().getFailed(),
                1
        );

        Assert.assertEquals(
                summary.getMetrics().getSkipped(),
                1
        );

        Assert.assertEquals(
                summary.getMetrics().getDurationSeconds(),
                3L
        );

        Assert.assertEquals(
                summary.getDetails().get("resultFiles"),
                1
        );
    }

    @Test
    public void shouldReturnPartialWhenOnlySkippedTestsExist()
            throws Exception {

        Path directory =
                Files.createTempDirectory("mapaf-api-skipped");

        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite
                    name="api.tests"
                    tests="3"
                    skipped="1"
                    failures="0"
                    errors="0"
                    time="1.0">
                </testsuite>
                """;

        Files.writeString(
                directory.resolve("TEST-api.tests.xml"),
                xml,
                StandardCharsets.UTF_8
        );

        ExecutionSummary summary =
                ApiSummaryGenerator.generate(directory);

        Assert.assertEquals(
                summary.getStatus(),
                ExecutionStatus.PARTIAL
        );

        Assert.assertEquals(
                summary.getMetrics().getPassed(),
                2
        );
    }

    @Test
    public void shouldReturnNotRunWhenNoResultsExist()
            throws Exception {

        Path directory =
                Files.createTempDirectory("mapaf-api-empty");

        ExecutionSummary summary =
                ApiSummaryGenerator.generate(directory);

        Assert.assertEquals(
                summary.getStatus(),
                ExecutionStatus.NOT_RUN
        );

        Assert.assertEquals(
                summary.getMetrics().getTotal(),
                0
        );
    }
}
