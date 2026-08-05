package core.ai.services;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.locator.LocatorAnalysisRequest;
import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorStrategy;

import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Adapter between the existing healing runtime and the structured
 * locator intelligence platform.
 */
public final class HealingAIService {

        private static final Logger logger = LoggerUtil.getLogger(HealingAIService.class);

        private final LocatorAnalysisService locatorAnalysisService;

        public HealingAIService() {
                this(
                                new LocatorAnalysisService());
        }

        public HealingAIService(
                        LocatorAnalysisService locatorAnalysisService) {

                this.locatorAnalysisService = Objects.requireNonNull(
                                locatorAnalysisService,
                                "Locator analysis service must not be null");
        }

        /**
         * Returns the highest-ranked locator recommendation.
         *
         * Retained for compatibility with existing healing callers.
         */
        public HealedLocatorCandidate recommendLocator(
                        By brokenLocator,
                        String screenClass,
                        String testName,
                        String platform,
                        String compressedPageSource) {

                List<HealedLocatorCandidate> candidates = recommendLocators(
                                brokenLocator,
                                screenClass,
                                testName,
                                platform,
                                compressedPageSource);

                return candidates.isEmpty()
                                ? null
                                : candidates.get(0);
        }

        /**
         * Returns all valid locator recommendations in ranked order.
         */
        public List<HealedLocatorCandidate> recommendLocators(
                        By brokenLocator,
                        String screenClass,
                        String testName,
                        String platform,
                        String compressedPageSource) {

                Objects.requireNonNull(
                                brokenLocator,
                                "Broken locator must not be null");

                LocatorAnalysisRequest request = LocatorAnalysisRequest.builder()
                                .platform(
                                                normalisePlatform(platform))
                                .screenName(
                                                normaliseRequired(
                                                                screenClass,
                                                                "Unknown screen"))
                                .elementDescription(
                                                buildElementDescription(
                                                                brokenLocator,
                                                                testName))
                                .existingLocator(
                                                brokenLocator.toString())
                                .failureMessage(
                                                "Element lookup failed for "
                                                                + brokenLocator)
                                .pageSource(
                                                normaliseRequired(
                                                                compressedPageSource,
                                                                "<page-source-unavailable/>"))
                                .build();

                LocatorAnalysisResult result = locatorAnalysisService.analyze(request);

                if (!result.isSuccessful()) {
                        logger.warn(
                                        "[Healing AI] Locator analysis failed: {}",
                                        result.getErrorMessage());

                        return List.of();
                }

                List<HealedLocatorCandidate> recommendations = new ArrayList<>();

                for (LocatorCandidate candidate : result.getCandidates()) {

                        if (candidate == null
                                        || candidate.getStrategy() == LocatorStrategy.UNKNOWN) {

                                logger.debug(
                                                "[Healing AI] Ignoring unsupported candidate: {}",
                                                candidate);

                                continue;
                        }

                        recommendations.add(
                                        toHealingCandidate(candidate));
                }

                if (recommendations.isEmpty()) {
                        logger.warn(
                                        "[Healing AI] Locator analysis returned no usable candidates");

                        return List.of();
                }

                logger.info(
                                "[Healing AI] Returning {} ranked locator candidates",
                                recommendations.size());

                return Collections.unmodifiableList(
                                recommendations);
        }

        private HealedLocatorCandidate toHealingCandidate(
                        LocatorCandidate candidate) {

                return new HealedLocatorCandidate(
                                candidate.getStrategy().name(),
                                candidate.getValue(),
                                candidate.getConfidence(),
                                candidate.getReasoning() == null
                                                ? "AI locator recommendation"
                                                : candidate.getReasoning());
        }

        private String buildElementDescription(
                        By brokenLocator,
                        String testName) {

                String normalisedTestName = normaliseRequired(
                                testName,
                                "Unknown test");

                return "Replacement locator for "
                                + brokenLocator
                                + " in test "
                                + normalisedTestName;
        }

        private String normalisePlatform(
                        String platform) {

                return normaliseRequired(
                                platform,
                                "android");
        }

        private String normaliseRequired(
                        String value,
                        String fallback) {

                if (value == null || value.isBlank()) {
                        return fallback;
                }

                return value.trim();
        }
}