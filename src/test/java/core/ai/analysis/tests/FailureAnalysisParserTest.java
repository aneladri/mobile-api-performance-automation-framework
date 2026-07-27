package core.ai.analysis.tests;

import core.ai.analysis.FailureAnalysisParser;
import core.ai.analysis.FailureAnalysisResult;
import core.ai.analysis.FailureConfidence;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisParserTest {

    private final FailureAnalysisParser parser =
            new FailureAnalysisParser();

    @Test
    public void shouldParseCompleteFailureAnalysis() {

        String response = """
                ## Root Cause

                The locator no longer matches the element.

                ## Confidence

                HIGH

                ## Recommended Fix

                Replace the XPath with a stable accessibility ID.

                ## Locator Healing

                YES

                ## Additional Evidence

                Capture the latest page source.
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getRootCause(),
                "The locator no longer matches the element."
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.HIGH
        );

        Assert.assertEquals(
                result.getRecommendedFix(),
                "Replace the XPath with a stable accessibility ID."
        );

        Assert.assertTrue(
                result.isLocatorHealingRecommended()
        );

        Assert.assertEquals(
                result.getAdditionalEvidence(),
                "Capture the latest page source."
        );

        Assert.assertEquals(
                result.getRawResponse(),
                response.trim()
        );
    }

    @Test
    public void shouldPreserveMultilineSections() {

        String response = """
                ## Root Cause

                The upload service timed out.
                The downstream storage dependency was unavailable.

                ## Confidence

                MEDIUM

                ## Recommended Fix

                Retry the upload with backoff.
                Validate MinIO health before submission.

                ## Locator Healing

                NO

                ## Additional Evidence

                Review storage logs.
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertTrue(
                result.getRootCause()
                        .contains(
                                "The downstream storage dependency"
                        )
        );

        Assert.assertTrue(
                result.getRecommendedFix()
                        .contains(
                                "Validate MinIO health"
                        )
        );

        Assert.assertFalse(
                result.isLocatorHealingRecommended()
        );
    }

    @Test
    public void shouldParseHeadingsIgnoringCase() {

        String response = """
                ## root cause
                Timeout

                ## confidence
                low

                ## recommended fix
                Increase timeout

                ## locator healing
                false

                ## additional evidence
                Review network logs
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.LOW
        );
    }

    @Test
    public void shouldReturnUnknownConfidence() {

        String response = """
                ## Root Cause
                Unknown dependency failure

                ## Confidence
                CERTAIN

                ## Recommended Fix
                Review logs

                ## Locator Healing
                NO

                ## Additional Evidence
                None
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.UNKNOWN
        );
    }

    @Test
    public void shouldReturnFailureForEmptyResponse() {

        FailureAnalysisResult result =
                parser.parse(" ");

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Failure analysis response was empty"
        );
    }

    @Test
    public void shouldReturnFailureWhenRootCauseIsMissing() {

        String response = """
                ## Confidence
                HIGH

                ## Recommended Fix
                Review the service logs
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Failure analysis response is missing section: Root Cause"
        );
    }

    @Test
    public void shouldReturnFailureWhenRecommendedFixIsMissing() {

        String response = """
                ## Root Cause
                Service unavailable

                ## Confidence
                HIGH
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Failure analysis response is missing section: Recommended Fix"
        );
    }

    @Test
    public void shouldDefaultOptionalSections() {

        String response = """
                ## Root Cause
                Validation failed

                ## Recommended Fix
                Correct the payload
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.UNKNOWN
        );

        Assert.assertFalse(
                result.isLocatorHealingRecommended()
        );

        Assert.assertNull(
                result.getAdditionalEvidence()
        );
    }

    @Test
    public void shouldInterpretRecommendedAsLocatorHealing() {

        String response = """
                ## Root Cause
                Locator changed

                ## Confidence
                HIGH

                ## Recommended Fix
                Replace locator

                ## Locator Healing
                RECOMMENDED

                ## Additional Evidence
                Current DOM
                """;

        FailureAnalysisResult result =
                parser.parse(response);

        Assert.assertTrue(
                result.isLocatorHealingRecommended()
        );
    }
}
