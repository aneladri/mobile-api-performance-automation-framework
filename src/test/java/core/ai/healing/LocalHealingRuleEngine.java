package core.ai.healing;

import core.driver.DriverManager;
import core.utils.LoggerUtil;
import mobile.locators.LocatorBuilder;
import mobile.locators.LocatorType;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalHealingRuleEngine {

    private static final Logger logger =
            LoggerUtil.getLogger(LocalHealingRuleEngine.class);

    private static final int PROBE_TIMEOUT_SECONDS = 5;

    public List<HealedLocatorCandidate> attempt(
            By brokenLocator,
            String pageSource
    ) {
        List<HealedLocatorCandidate> results =
                new ArrayList<>();

        String rawLocator =
                brokenLocator.toString();

        logger.debug(
                "[Rule Engine] Running local rules for: {}",
                rawLocator
        );

        results.addAll(
                tryIdSuffixVariants(rawLocator)
        );

        if (results.isEmpty()) {
            results.addAll(
                    tryTextXPathFromSource(
                            rawLocator,
                            pageSource
                    )
            );
        }

        if (results.isEmpty()) {
            results.addAll(
                    tryAccessibilityScan(
                            rawLocator,
                            pageSource
                    )
            );
        }

        if (results.isEmpty()) {
            results.addAll(
                    tryClassIndex(
                            rawLocator,
                            pageSource
                    )
            );
        }

        return results;
    }

    private List<HealedLocatorCandidate> tryIdSuffixVariants(
            String rawLocator
    ) {
        List<HealedLocatorCandidate> found =
                new ArrayList<>();

        if (!rawLocator.startsWith("By.id:")) {
            return found;
        }

        String idValue =
                rawLocator.replace("By.id:", "").trim();

        List<String> variants =
                new ArrayList<>();

        if (idValue.contains(":id/")) {
            variants.add(
                    idValue.substring(
                            idValue.indexOf(":id/") + 4
                    )
            );
        }

        if (idValue.contains("/")) {
            variants.add(
                    idValue.substring(
                            idValue.lastIndexOf('/') + 1
                    )
            );
        }

        String baseName =
                idValue.contains("/")
                        ? idValue.substring(
                        idValue.lastIndexOf('/') + 1
                )
                        : idValue;

        for (String prefix : List.of(
                "com.app:id/",
                "id/",
                "android:id/"
        )) {
            variants.add(prefix + baseName);
        }

        for (String variant : variants) {
            if (variant.equals(idValue)) {
                continue;
            }

            WebElement element =
                    probe(By.id(variant));

            if (element != null) {
                logger.info(
                        "[Rule Engine] ID variant succeeded: {}",
                        variant
                );

                found.add(
                        new HealedLocatorCandidate(
                                "ID",
                                variant,
                                82,
                                "ID suffix variant derived locally"
                        )
                );

                break;
            }
        }

        return found;
    }

    private List<HealedLocatorCandidate> tryTextXPathFromSource(
            String rawLocator,
            String pageSource
    ) {
        List<HealedLocatorCandidate> found =
                new ArrayList<>();

        if (pageSource == null || pageSource.isBlank()) {
            return found;
        }

        String token =
                extractToken(rawLocator);

        if (token == null || token.length() < 3) {
            return found;
        }

        if (!pageSource.toLowerCase()
                .contains(token.toLowerCase())) {
            return found;
        }

        List<String> xpaths =
                List.of(
                        "//*[@text='" + token + "']",
                        "//*[contains(@text,'" + token + "')]",
                        "//*[@label='" + token + "']",
                        "//*[@value='" + token + "']"
                );

        for (String xpath : xpaths) {
            WebElement element =
                    probe(By.xpath(xpath));

            if (element != null) {
                logger.info(
                        "[Rule Engine] Text XPath succeeded: {}",
                        xpath
                );

                found.add(
                        new HealedLocatorCandidate(
                                "XPATH",
                                xpath,
                                75,
                                "Text XPath derived locally"
                        )
                );

                break;
            }
        }

        return found;
    }

    private List<HealedLocatorCandidate> tryAccessibilityScan(
            String rawLocator,
            String pageSource
    ) {
        List<HealedLocatorCandidate> found =
                new ArrayList<>();

        if (pageSource == null || pageSource.isBlank()) {
            return found;
        }

        String token =
                extractToken(rawLocator);

        if (token == null || token.length() < 3) {
            return found;
        }

        Pattern contentDesc =
                Pattern.compile(
                        "content-desc=\"([^\"]*"
                                + Pattern.quote(token)
                                + "[^\"]*)\"",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                contentDesc.matcher(pageSource);

        if (matcher.find()) {
            String accessibilityId =
                    matcher.group(1);

            WebElement element =
                    probe(
                            LocatorBuilder.build(
                                    LocatorType.ACCESSIBILITY_ID,
                                    accessibilityId
                            )
                    );

            if (element != null) {
                logger.info(
                        "[Rule Engine] Accessibility scan succeeded: {}",
                        accessibilityId
                );

                found.add(
                        new HealedLocatorCandidate(
                                "ACCESSIBILITY_ID",
                                accessibilityId,
                                88,
                                "Accessibility ID scanned from page source"
                        )
                );
            }
        }

        return found;
    }

    private List<HealedLocatorCandidate> tryClassIndex(
            String rawLocator,
            String pageSource
    ) {
        List<HealedLocatorCandidate> found =
                new ArrayList<>();

        if (pageSource == null || pageSource.isBlank()) {
            return found;
        }

        if (!rawLocator.startsWith("By.xpath:")) {
            return found;
        }

        String token =
                extractToken(rawLocator);

        if (token == null) {
            return found;
        }

        Pattern classPattern =
                Pattern.compile(
                        "class=\"([^\"]+)\"[^>]*(?:text|content-desc)=\"[^\"]*"
                                + Pattern.quote(token)
                                + "[^\"]*\"",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                classPattern.matcher(pageSource);

        if (matcher.find()) {
            String className =
                    matcher.group(1);

            String xpath =
                    "(//" + className + ")[1]";

            WebElement element =
                    probe(By.xpath(xpath));

            if (element != null) {
                logger.info(
                        "[Rule Engine] Class index succeeded: {}",
                        xpath
                );

                found.add(
                        new HealedLocatorCandidate(
                                "XPATH",
                                xpath,
                                65,
                                "Class and index derived locally"
                        )
                );
            }
        }

        return found;
    }

    private WebElement probe(By locator) {
        try {
            WebDriverWait wait =
                    new WebDriverWait(
                            DriverManager.getDriver(),
                            Duration.ofSeconds(PROBE_TIMEOUT_SECONDS)
                    );

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            );

        } catch (Exception e) {
            return null;
        }
    }

    private String extractToken(String rawLocator) {
        if (rawLocator == null) {
            return null;
        }

        Pattern textAttribute =
                Pattern.compile(
                        "@(?:text|content-desc|label|value)='([^']+)'",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                textAttribute.matcher(rawLocator);

        if (matcher.find()) {
            return matcher.group(1);
        }

        String stripped =
                rawLocator.replaceFirst(
                        "^By\\.\\w[\\w ]*:\\s*",
                        ""
                );

        if (stripped.contains(":id/")) {
            return stripped.substring(
                    stripped.indexOf(":id/") + 4
            );
        }

        if (stripped.contains("/")) {
            return stripped.substring(
                    stripped.lastIndexOf('/') + 1
            );
        }

        return stripped.length() >= 3
                ? stripped
                : null;
    }
}
