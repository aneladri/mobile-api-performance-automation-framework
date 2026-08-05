package web.tests;

import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.reporting.WebSummaryGenerator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebSummaryGeneratorTest {

        @Test
        public void shouldGenerateWebSummaryFromGradleXml()
                        throws Exception {

                Path resultsDirectory = Files.createTempDirectory(
                                "mapaf-web-results");

                Files.writeString(
                                resultsDirectory.resolve(
                                                "TEST-web.tests.LoginTest.xml"),
                                """
                                                <?xml version="1.0" encoding="UTF-8"?>
                                                <testsuite
                                                    name="web.tests.LoginTest"
                                                    tests="5"
                                                    skipped="1"
                                                    failures="1"
                                                    errors="0"
                                                    time="12.5">
                                                </testsuite>
                                                """,
                                StandardCharsets.UTF_8);

                ExecutionSummary summary = WebSummaryGenerator.generate(
                                resultsDirectory);

                Assert.assertEquals(
                                summary.getModule(),
                                "Web");

                Assert.assertEquals(
                                summary.getStatus(),
                                ExecutionStatus.FAIL);

                Assert.assertEquals(
                                summary.getMetrics().getTotal(),
                                5);

                Assert.assertEquals(
                                summary.getMetrics().getPassed(),
                                3);

                Assert.assertEquals(
                                summary.getMetrics().getFailed(),
                                1);

                Assert.assertEquals(
                                summary.getMetrics().getSkipped(),
                                1);

                Assert.assertEquals(
                                summary.getMetrics()
                                                .getDurationSeconds(),
                                13L);

                Assert.assertEquals(
                                summary.getDetails()
                                                .get("resultFiles"),
                                1);

                Assert.assertEquals(
                                summary.getDetails()
                                                .get("framework"),
                                "Playwright Java TestNG");
        }

        @Test
        public void shouldReturnNotRunWhenNoResultsExist()
                        throws Exception {

                Path resultsDirectory = Files.createTempDirectory(
                                "mapaf-empty-web-results");

                ExecutionSummary summary = WebSummaryGenerator.generate(
                                resultsDirectory);

                Assert.assertEquals(
                                summary.getStatus(),
                                ExecutionStatus.NOT_RUN);

                Assert.assertEquals(
                                summary.getMetrics().getTotal(),
                                0);
        }

        @Test
        public void shouldReturnPartialWhenTestsAreSkipped()
                        throws Exception {

                Path resultsDirectory = Files.createTempDirectory(
                                "mapaf-skipped-web-results");

                Files.writeString(
                                resultsDirectory.resolve(
                                                "TEST-web.tests.SearchTest.xml"),
                                """
                                                <?xml version="1.0" encoding="UTF-8"?>
                                                <testsuite
                                                    name="web.tests.SearchTest"
                                                    tests="4"
                                                    skipped="1"
                                                    failures="0"
                                                    errors="0"
                                                    time="4.0">
                                                </testsuite>
                                                """,
                                StandardCharsets.UTF_8);

                ExecutionSummary summary = WebSummaryGenerator.generate(
                                resultsDirectory);

                Assert.assertEquals(
                                summary.getStatus(),
                                ExecutionStatus.PARTIAL);

                Assert.assertEquals(
                                summary.getMetrics().getPassed(),
                                3);
        }

        @Test
        public void shouldWriteWebSummaryFile()
                        throws Exception {

                Path resultsDirectory = Files.createTempDirectory(
                                "mapaf-web-write-results");

                Files.writeString(
                                resultsDirectory.resolve(
                                                "TEST-web.tests.FormTest.xml"),
                                """
                                                <?xml version="1.0" encoding="UTF-8"?>
                                                <testsuite
                                                    name="web.tests.FormTest"
                                                    tests="2"
                                                    skipped="0"
                                                    failures="0"
                                                    errors="0"
                                                    time="2.5">
                                                </testsuite>
                                                """,
                                StandardCharsets.UTF_8);

                Path outputFile = Files.createTempDirectory(
                                "mapaf-web-summary").resolve("summary.json");

                Path generatedFile = WebSummaryGenerator
                                .generateAndWrite(
                                                resultsDirectory,
                                                outputFile);

                Assert.assertTrue(
                                Files.exists(generatedFile));

                String json = Files.readString(
                                generatedFile,
                                StandardCharsets.UTF_8);

                Assert.assertTrue(
                                json.contains("\"module\" : \"Web\""));

                Assert.assertTrue(
                                json.contains("\"status\" : \"PASS\""));
        }
}
