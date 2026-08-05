package core.ai.locator.tests;

import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorCandidateTest {

    @Test
    public void shouldCreateValidLocatorCandidate() {
        LocatorCandidate candidate =
                LocatorCandidate.builder()
                        .strategy(LocatorStrategy.ACCESSIBILITY_ID)
                        .value("submit-order")
                        .confidence(97)
                        .reasoning(
                                "Accessibility identifier is stable across builds"
                        )
                        .fallback(false)
                        .build();

        Assert.assertEquals(
                candidate.getStrategy(),
                LocatorStrategy.ACCESSIBILITY_ID
        );

        Assert.assertEquals(
                candidate.getValue(),
                "submit-order"
        );

        Assert.assertEquals(
                candidate.getConfidence(),
                97
        );

        Assert.assertEquals(
                candidate.getReasoning(),
                "Accessibility identifier is stable across builds"
        );

        Assert.assertFalse(candidate.isFallback());
    }

    @Test
    public void shouldDefaultNullStrategyToUnknown() {
        LocatorCandidate candidate =
                LocatorCandidate.builder()
                        .value("submit-order")
                        .confidence(75)
                        .build();

        Assert.assertEquals(
                candidate.getStrategy(),
                LocatorStrategy.UNKNOWN
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator value must not be blank"
    )
    public void shouldRejectBlankLocatorValue() {
        LocatorCandidate.builder()
                .strategy(LocatorStrategy.ID)
                .value(" ")
                .confidence(80)
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator confidence must be between 0 and 100"
    )
    public void shouldRejectConfidenceBelowZero() {
        LocatorCandidate.builder()
                .strategy(LocatorStrategy.ID)
                .value("submit")
                .confidence(-1)
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator confidence must be between 0 and 100"
    )
    public void shouldRejectConfidenceAboveOneHundred() {
        LocatorCandidate.builder()
                .strategy(LocatorStrategy.ID)
                .value("submit")
                .confidence(101)
                .build();
    }
}
