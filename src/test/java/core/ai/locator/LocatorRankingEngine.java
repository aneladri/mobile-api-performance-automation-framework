package core.ai.locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Ranks locator candidates using framework-defined stability rules,
 * AI confidence, and fallback penalties.
 */
public final class LocatorRankingEngine {

    private static final int FALLBACK_PENALTY = 500;

    private static final Map<LocatorStrategy, Integer> STRATEGY_WEIGHTS =
            Map.of(
                    LocatorStrategy.ACCESSIBILITY_ID, 100,
                    LocatorStrategy.ID, 95,
                    LocatorStrategy.RESOURCE_ID, 90,
                    LocatorStrategy.NAME, 80,
                    LocatorStrategy.CSS_SELECTOR, 70,
                    LocatorStrategy.CLASS_NAME, 60,
                    LocatorStrategy.TEXT, 50,
                    LocatorStrategy.XPATH, 10,
                    LocatorStrategy.UNKNOWN, 0
            );

    /**
     * Returns candidates ranked from strongest to weakest.
     *
     * Null candidates inside the supplied list are ignored.
     */
    public List<LocatorCandidate> rank(
            List<LocatorCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        List<LocatorCandidate> ranked =
                new ArrayList<>();

        for (LocatorCandidate candidate : candidates) {
            if (candidate != null) {
                ranked.add(candidate);
            }
        }

        ranked.sort(
                Comparator.comparingInt(
                                this::calculateScore
                        )
                        .reversed()
        );

        return Collections.unmodifiableList(ranked);
    }

    /**
     * Returns the highest-ranked candidate, or null when none exist.
     */
    public LocatorCandidate bestCandidate(
            List<LocatorCandidate> candidates) {

        List<LocatorCandidate> ranked =
                rank(candidates);

        return ranked.isEmpty()
                ? null
                : ranked.get(0);
    }

    int calculateScore(
            LocatorCandidate candidate) {

        if (candidate == null) {
            return Integer.MIN_VALUE;
        }

        int strategyWeight =
                STRATEGY_WEIGHTS.getOrDefault(
                        candidate.getStrategy(),
                        0
                );

        int fallbackPenalty =
                candidate.isFallback()
                        ? FALLBACK_PENALTY
                        : 0;

        return strategyWeight * 1000
                + candidate.getConfidence()
                - fallbackPenalty;
    }
}
