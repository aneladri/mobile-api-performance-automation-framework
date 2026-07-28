package core.ai.locator.tests;

import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class LocatorAnalysisResultTest {

    @Test
    public void shouldCreateSuccessfulLocatorAnalysisResult() {
        LocatorCandidate primary =
                createCandidate(
                        LocatorStrategy.ACCESSIBILITY_ID,
                        "submit-order",
                        97,
                        false
                );

        LocatorCandidate fallback =
                createCandidate(
                        LocatorStrategy.RESOURCE_ID,
                        "com.example:id/submit_order",
                        88,
                        true
                );

        LocatorAnalysisResult result =
                LocatorAnalysisResult.builder()
                        .successful(true)
                        .addCandidate(primary)
                        .addCandidate(fallback)
                        .summary(
                                "Accessibility ID is the most stable locator"
                        )
                        .rawResponse(
                                "AI locator analysis response"
                        )
                        .build();

        Assert.assertTrue(result.isSuccessful());

        Assert.assertEquals(
                result.getCandidates().size(),
                2
        );

        Assert.assertEquals(
                result.getSummary(),
                "Accessibility ID is the most stable locator"
        );

        Assert.assertNull(
                result.getErrorMessage()
        );
    }

    @Test
    public void shouldExposeFirstCandidateAsBestCandidate() {
        LocatorCandidate primary =
                createCandidate(
                        LocatorStrategy.ACCESSIBILITY_ID,
                        "submit-order",
                        97,
                        false
                );

        LocatorCandidate fallback =
                createCandidate(
                        LocatorStrategy.XPATH,
                        "//button[@text='Submit']",
                        60,
                        true
                );

        LocatorAnalysisResult result =
                LocatorAnalysisResult.builder()
                        .successful(true)
                        .candidates(
                                List.of(primary, fallback)
                        )
                        .rawResponse(
                                "AI locator analysis response"
                        )
                        .build();

        Assert.assertSame(
                result.getBestCandidate(),
                primary
        );
    }

    @Test(
            expectedExceptions = UnsupportedOperationException.class
    )
    public void shouldReturnImmutableCandidateList() {
        LocatorCandidate candidate =
                createCandidate(
                        LocatorStrategy.ID,
                        "submit",
                        90,
                        false
                );

        LocatorAnalysisResult result =
                LocatorAnalysisResult.builder()
                        .successful(true)
                        .addCandidate(candidate)
                        .rawResponse("AI response")
                        .build();

        result.getCandidates().add(candidate);
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Successful locator analysis requires at least one candidate"
    )
    public void shouldRejectSuccessfulResultWithoutCandidates() {
        LocatorAnalysisResult.builder()
                .successful(true)
                .rawResponse("AI response")
                .build();
    }

    @Test
    public void shouldCreateFailureResult() {
        LocatorAnalysisResult result =
                LocatorAnalysisResult.failure(
                        "AI provider unavailable"
                );

        Assert.assertFalse(result.isSuccessful());

        Assert.assertEquals(
                result.getErrorMessage(),
                "AI provider unavailable"
        );

        Assert.assertTrue(
                result.getCandidates().isEmpty()
        );

        Assert.assertNull(
                result.getBestCandidate()
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Error message must not be blank"
    )
    public void shouldRejectFailureWithoutErrorMessage() {
        LocatorAnalysisResult.builder()
                .successful(false)
                .build();
    }

    private LocatorCandidate createCandidate(
            LocatorStrategy strategy,
            String value,
            int confidence,
            boolean fallback) {

        return LocatorCandidate.builder()
                .strategy(strategy)
                .value(value)
                .confidence(confidence)
                .reasoning(
                        "Candidate selected based on element attributes"
                )
                .fallback(fallback)
                .build();
    }
}
