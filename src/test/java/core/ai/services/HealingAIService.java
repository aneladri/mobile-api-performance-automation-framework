package core.ai.services;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class HealingAIService {

    private static final Logger logger =
            LoggerUtil.getLogger(HealingAIService.class);

    private final AIProvider provider =
            AIProviderFactory.getProvider();

    public HealedLocatorCandidate recommendLocator(
            By brokenLocator,
            String screenClass,
            String testName,
            String platform,
            String compressedPageSource
    ) {
        AIResponse response =
                provider.complete(
                        new AIRequest(
                                systemMessage(),
                                prompt(
                                        brokenLocator,
                                        screenClass,
                                        testName,
                                        platform,
                                        compressedPageSource
                                )
                        )
                );

        if (!response.isSuccessful()) {
            logger.warn(
                    "[Healing AI] Provider failed: {}",
                    response.getErrorMessage()
            );
            return null;
        }

        return parseCandidate(
                response.getContent()
        );
    }

    private String systemMessage() {
        return """
                You are a mobile automation locator healing assistant.
                Return exactly one locator recommendation.
                Use this exact response format:
                locatorType=ACCESSIBILITY_ID
                locatorValue=value_here
                confidence=85
                explanation=short explanation

                Supported locator types:
                ID
                XPATH
                CLASS_NAME
                ACCESSIBILITY_ID
                """;
    }

    private String prompt(
            By brokenLocator,
            String screenClass,
            String testName,
            String platform,
            String compressedPageSource
    ) {
        return """
                Broken locator:
                %s

                Screen class:
                %s

                Test name:
                %s

                Platform:
                %s

                Compressed page source:
                %s

                Recommend the best replacement locator.
                Prefer ACCESSIBILITY_ID when available.
                Prefer stable IDs over text XPath.
                Avoid brittle XPath unless no better option exists.
                """.formatted(
                brokenLocator,
                screenClass,
                testName,
                platform,
                compressedPageSource
        );
    }

    private HealedLocatorCandidate parseCandidate(
            String content
    ) {
        String locatorType =
                extract(content, "locatorType");

        String locatorValue =
                extract(content, "locatorValue");

        String confidenceText =
                extract(content, "confidence");

        String explanation =
                extract(content, "explanation");

        if (locatorType == null
                || locatorValue == null
                || confidenceText == null) {
            logger.warn(
                    "[Healing AI] Could not parse response: {}",
                    content
            );
            return null;
        }

        int confidence;

        try {
            confidence =
                    Integer.parseInt(
                            confidenceText.trim()
                    );
        } catch (NumberFormatException e) {
            confidence = 0;
        }

        return new HealedLocatorCandidate(
                locatorType.trim(),
                locatorValue.trim(),
                confidence,
                explanation != null
                        ? explanation.trim()
                        : "AI locator recommendation"
        );
    }

    private String extract(
            String content,
            String key
    ) {
        if (content == null) {
            return null;
        }

        String prefix =
                key + "=";

        return content.lines()
                .filter(line -> line.trim().startsWith(prefix))
                .map(line -> line.trim().substring(prefix.length()))
                .findFirst()
                .orElse(null);
    }
}
