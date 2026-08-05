package core.ai.analysis.tests;

import core.ai.analysis.FailureAnalysisResult;
import core.ai.analysis.FailureConfidence;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisResultTest {

    @Test
    public void shouldCreateSuccessfulResult() {

        FailureAnalysisResult result =
                FailureAnalysisResult.builder()
                        .successful(true)
                        .rootCause(
                                "Locator no longer matches the element"
                        )
                        .confidence(
                                FailureConfidence.HIGH
                        )
                        .recommendedFix(
                                "Replace the locator with a stable ID"
                        )
                        .locatorHealingRecommended(true)
                        .additionalEvidence(
                                "Capture current page source"
                        )
                        .rawResponse(
                                "Full Claude response"
                        )
                        .build();

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.HIGH
        );

        Assert.assertTrue(
                result.isLocatorHealingRecommended()
        );

        Assert.assertNull(
                result.getErrorMessage()
        );
    }

    @Test
    public void shouldCreateFailureResult() {

        FailureAnalysisResult result =
                FailureAnalysisResult.failure(
                        "Claude integration is disabled"
                );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Claude integration is disabled"
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.UNKNOWN
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Root cause must not be blank"
    )
    public void shouldRejectSuccessfulResultWithoutRootCause() {

        FailureAnalysisResult.builder()
                .successful(true)
                .recommendedFix("Fix")
                .rawResponse("Response")
                .build();
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Error message must not be blank"
    )
    public void shouldRejectFailureWithoutErrorMessage() {

        FailureAnalysisResult.builder()
                .successful(false)
                .build();
    }
}
