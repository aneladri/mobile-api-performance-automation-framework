package core.ai.locator.tests;

import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorRankingEngine;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LocatorRankingEngineTest {

    private final LocatorRankingEngine engine =
            new LocatorRankingEngine();

    @Test
    public void shouldRankAccessibilityIdAboveXpath() {

        LocatorCandidate xpath =
                candidate(
                        LocatorStrategy.XPATH,
                        "//button[@text='Submit']",
                        99,
                        false
                );

        LocatorCandidate accessibilityId =
                candidate(
                        LocatorStrategy.ACCESSIBILITY_ID,
                        "submit-order",
                        70,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(xpath, accessibilityId)
                );

        Assert.assertSame(
                ranked.get(0),
                accessibilityId
        );

        Assert.assertSame(
                ranked.get(1),
                xpath
        );
    }

    @Test
    public void shouldRankByConfidenceWhenStrategyMatches() {

        LocatorCandidate lowerConfidence =
                candidate(
                        LocatorStrategy.ID,
                        "submit-low",
                        70,
                        false
                );

        LocatorCandidate higherConfidence =
                candidate(
                        LocatorStrategy.ID,
                        "submit-high",
                        95,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(
                                lowerConfidence,
                                higherConfidence
                        )
                );

        Assert.assertSame(
                ranked.get(0),
                higherConfidence
        );
    }

    @Test
    public void shouldPenaliseFallbackCandidate() {

        LocatorCandidate fallback =
                candidate(
                        LocatorStrategy.ID,
                        "fallback-submit",
                        95,
                        true
                );

        LocatorCandidate primary =
                candidate(
                        LocatorStrategy.ID,
                        "primary-submit",
                        90,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(fallback, primary)
                );

        Assert.assertSame(
                ranked.get(0),
                primary
        );
    }

    @Test
    public void shouldReturnImmutableRankedList() {

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(
                                candidate(
                                        LocatorStrategy.ID,
                                        "submit",
                                        90,
                                        false
                                )
                        )
                );

        Assert.expectThrows(
                UnsupportedOperationException.class,
                () -> ranked.clear()
        );
    }

    @Test
    public void shouldReturnBestCandidate() {

        LocatorCandidate xpath =
                candidate(
                        LocatorStrategy.XPATH,
                        "//button",
                        99,
                        false
                );

        LocatorCandidate accessibilityId =
                candidate(
                        LocatorStrategy.ACCESSIBILITY_ID,
                        "submit-order",
                        80,
                        false
                );

        LocatorCandidate best =
                engine.bestCandidate(
                        List.of(xpath, accessibilityId)
                );

        Assert.assertSame(
                best,
                accessibilityId
        );
    }

    @Test
    public void shouldReturnNullBestCandidateForEmptyInput() {

        Assert.assertNull(
                engine.bestCandidate(List.of())
        );
    }

    @Test
    public void shouldHandleEmptyInput() {

        List<LocatorCandidate> ranked =
                engine.rank(List.of());

        Assert.assertTrue(
                ranked.isEmpty()
        );
    }

    @Test
    public void shouldHandleNullInput() {

        List<LocatorCandidate> ranked =
                engine.rank(null);

        Assert.assertTrue(
                ranked.isEmpty()
        );
    }

    @Test
    public void shouldIgnoreNullCandidates() {

        LocatorCandidate candidate =
                candidate(
                        LocatorStrategy.ID,
                        "submit",
                        90,
                        false
                );

        List<LocatorCandidate> values =
                new ArrayList<>();

        values.add(null);
        values.add(candidate);

        List<LocatorCandidate> ranked =
                engine.rank(values);

        Assert.assertEquals(
                ranked.size(),
                1
        );

        Assert.assertSame(
                ranked.get(0),
                candidate
        );
    }

    @Test
    public void shouldPreserveEqualRankingOrder() {

        LocatorCandidate first =
                candidate(
                        LocatorStrategy.ID,
                        "first",
                        90,
                        false
                );

        LocatorCandidate second =
                candidate(
                        LocatorStrategy.ID,
                        "second",
                        90,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(first, second)
                );

        Assert.assertSame(
                ranked.get(0),
                first
        );

        Assert.assertSame(
                ranked.get(1),
                second
        );
    }

    @Test
    public void shouldKeepDuplicateCandidates() {

        LocatorCandidate duplicate =
                candidate(
                        LocatorStrategy.ID,
                        "submit",
                        90,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(duplicate, duplicate)
                );

        Assert.assertEquals(
                ranked.size(),
                2
        );

        Assert.assertSame(
                ranked.get(0),
                duplicate
        );

        Assert.assertSame(
                ranked.get(1),
                duplicate
        );
    }

    @Test
    public void shouldPreferStableLocatorOverHigherConfidenceXpath() {

        LocatorCandidate xpath =
                candidate(
                        LocatorStrategy.XPATH,
                        "//div[3]/button[2]",
                        100,
                        false
                );

        LocatorCandidate resourceId =
                candidate(
                        LocatorStrategy.RESOURCE_ID,
                        "com.demo:id/submit",
                        60,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(xpath, resourceId)
                );

        Assert.assertSame(
                ranked.get(0),
                resourceId
        );
    }

    @Test
    public void shouldRankUnknownStrategyLast() {

        LocatorCandidate unknown =
                candidate(
                        LocatorStrategy.UNKNOWN,
                        "custom-locator",
                        100,
                        false
                );

        LocatorCandidate xpath =
                candidate(
                        LocatorStrategy.XPATH,
                        "//button",
                        20,
                        false
                );

        List<LocatorCandidate> ranked =
                engine.rank(
                        List.of(unknown, xpath)
                );

        Assert.assertSame(
                ranked.get(0),
                xpath
        );

        Assert.assertSame(
                ranked.get(1),
                unknown
        );
    }

    private LocatorCandidate candidate(
            LocatorStrategy strategy,
            String value,
            int confidence,
            boolean fallback) {

        return LocatorCandidate.builder()
                .strategy(strategy)
                .value(value)
                .confidence(confidence)
                .reasoning(
                        "Candidate generated for ranking test"
                )
                .fallback(fallback)
                .build();
    }
}
