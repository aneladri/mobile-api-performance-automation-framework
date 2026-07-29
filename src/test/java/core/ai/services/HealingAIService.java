package core.ai.services;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.locator.LocatorAnalysisRequest;
import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Objects;

/**
 * Adapter between the existing healing runtime and the structured
 * locator intelligence platform.
 */
public final class HealingAIService {

    private static final Logger logger =
            LoggerUtil.getLogger(HealingAIService.class);

    private final LocatorAnalysisService locatorAnalysisService;

    public HealingAIService() {
        this(
                new LocatorAnalysisService()
        );
    }

    public HealingAIService(
            LocatorAnalysisService locatorAnalysisService) {

        this.locatorAnalysisService =
                Objects.requireNonNull(
                        locatorAnalysisService,
                        "Locator analysis service must not be null"
                );
    }

    public HealedLocatorCandidate recommendLocator(
            By brokenLocator,
            String screenClass,
            String testName,
            String platform,
            String compressedPageSource) {

        Objects.requireNonNull(
                brokenLocator,
                "Broken locator must not be null"
        );

        LocatorAnalysisRequest request =
                LocatorAnalysisRequest.builder()
                        .platform(
                                normalisePlatform(platform)
                        )
                        .screenName(
                                normaliseRequired(
                                        screenClass,
                                        "Unknown screen"
                                )
                        )
                        .elementDescription(
                                buildElementDescription(
                                        brokenLocator,
                                        testName
                                )
                        )
                        .existingLocator(
                                brokenLocator.toString()
                        )
                        .failureMessage(
                                "Element lookup failed for "
                                        + brokenLocator
                        )
                        .pageSource(
                                normaliseRequired(
                                        compressedPageSource,
                                        "<page-source-unavailable/>"
                                )
                        )
                        .build();

        LocatorAnalysisResult result =
                locatorAnalysisService.analyze(
                        request
                );

        if (!result.isSuccessful()) {
            logger.warn(
                    "[Healing AI] Locator analysis failed: {}",
                    result.getErrorMessage()
            );

            return null;
        }

        LocatorCandidate bestCandidate =
                result.getBestCandidate();

        if (bestCandidate == null) {
            logger.warn(
                    "[Healing AI] Locator analysis returned no candidates"
            );

            return null;
        }

        return toHealingCandidate(
                bestCandidate
        );
    }

    private HealedLocatorCandidate toHealingCandidate(
            LocatorCandidate candidate) {

        return new HealedLocatorCandidate(
                candidate.getStrategy().name(),
                candidate.getValue(),
                candidate.getConfidence(),
                candidate.getReasoning() == null
                        ? "AI locator recommendation"
                        : candidate.getReasoning()
        );
    }

    private String buildElementDescription(
            By brokenLocator,
            String testName) {

        String normalisedTestName =
                normaliseRequired(
                        testName,
                        "Unknown test"
                );

        return "Replacement locator for "
                + brokenLocator
                + " in test "
                + normalisedTestName;
    }

    private String normalisePlatform(
            String platform) {

        return normaliseRequired(
                platform,
                "android"
        );
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