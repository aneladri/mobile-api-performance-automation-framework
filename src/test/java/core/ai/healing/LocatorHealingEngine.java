package core.ai.healing;

import core.ai.services.HealingAIService;
import core.config.ConfigManager;
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
import java.util.List;

public class LocatorHealingEngine {

        private static final Logger logger = LoggerUtil.getLogger(LocatorHealingEngine.class);

        private static final int RETRY_TIMEOUT_SECONDS = 10;
        private static final int MIN_CONFIDENCE = 60;

        private final LocalHealingRuleEngine ruleEngine = new LocalHealingRuleEngine();

        private final HealingAIService aiService = new HealingAIService();

        public WebElement heal(
                        By brokenLocator,
                        String screenClass,
                        String testName) {
                HealingMetricsCollector.recordHealingAttempt();

                String locatorKey = brokenLocator.toString();

                logger.info(
                                "[Healing] Starting tiered healing for: {}",
                                locatorKey);

                WebElement cached = tryCachedLocator(locatorKey);

                if (cached != null) {
                        HealingMetricsCollector.recordCacheHit();

                        logger.info(
                                        "[Healing] Tier 1 HIT - cache");
                        return cached;
                }

                String rawPageSource = capturePageSource();

                List<HealedLocatorCandidate> ruleCandidates = ruleEngine.attempt(
                                brokenLocator,
                                rawPageSource);

                for (HealedLocatorCandidate candidate : ruleCandidates) {
                        WebElement element = tryCandidate(candidate);

                        if (element != null) {
                                HealingMetricsCollector.recordRuleHit();

                                logger.info(
                                                "[Healing] Tier 2 HIT - local rule: {}",
                                                candidate);

                                HealedLocatorStore.store(
                                                locatorKey,
                                                candidate,
                                                screenClass,
                                                testName);

                                return element;
                        }
                }

                logger.info(
                                "[Healing] Tier 2 MISS - checking API budget");

                if (!HealingBudgetGuard.allowApiCall(locatorKey)) {
                        HealingMetricsCollector.recordBudgetBlock();

                        logger.warn(
                                        "[Healing] Budget gate blocked API call for: {}",
                                        locatorKey);
                        return null;
                }

                HealingMetricsCollector.recordClaudeCall();

                String platform = ConfigManager.get("platform") != null
                                ? ConfigManager.get("platform")
                                : "android";

                String compressedSource = PageSourceCompressor.compress(rawPageSource);

                logger.info(
                                "[Healing] Tier 3 - calling Claude AI. Platform: {}, source chars: {}",
                                platform,
                                compressedSource.length());

                long aiStartTime = System.currentTimeMillis();

                List<HealedLocatorCandidate> aiCandidates = aiService.recommendLocators(
                                brokenLocator,
                                screenClass,
                                testName,
                                platform,
                                compressedSource);

                long aiDuration = System.currentTimeMillis() - aiStartTime;

                HealingMetricsCollector.recordAiHealingDuration(
                                aiDuration);

                HealingMetricsCollector.recordAiCandidatesGenerated(
                                aiCandidates.size());

                if (aiCandidates.isEmpty()) {
                        logger.warn(
                                        "[Healing] Tier 3 MISS - AI returned no usable recommendations");
                        return null;
                }

                int attemptedCandidates = 0;

                for (int index = 0; index < aiCandidates.size(); index++) {

                        HealedLocatorCandidate candidate = aiCandidates.get(index);

                        int rank = index + 1;

                        if (candidate.getConfidence() < MIN_CONFIDENCE) {
                                logger.info(
                                                "[Healing] Tier 3 SKIP - rank {}, confidence {} below threshold {}: {}",
                                                rank,
                                                candidate.getConfidence(),
                                                MIN_CONFIDENCE,
                                                candidate);

                                continue;
                        }

                        attemptedCandidates++;

                        logger.info(
                                        "[Healing] Tier 3 TRY - rank {}/{}, candidate: {}",
                                        rank,
                                        aiCandidates.size(),
                                        candidate);

                        HealingMetricsCollector.recordAiCandidateAttempt();

                        WebElement healedElement = tryCandidate(candidate);

                        if (healedElement == null) {
                                logger.info(
                                                "[Healing] Tier 3 candidate rank {} did not resolve: {}",
                                                rank,
                                                candidate);

                                continue;
                        }

                        HealingMetricsCollector.recordClaudeHit();

                        HealingMetricsCollector.recordAiHealingSuccess(
                                        rank,
                                        candidate.getConfidence());

                        logger.info(
                                        "[Healing] Tier 3 HIT - AI candidate rank {} after {} attempt(s): {}",
                                        rank,
                                        attemptedCandidates,
                                        candidate);

                        HealedLocatorStore.store(
                                        locatorKey,
                                        candidate,
                                        screenClass,
                                        testName);

                        return healedElement;
                }

                logger.warn(
                                "[Healing] Tier 3 MISS - none of {} eligible candidate(s) resolved",
                                attemptedCandidates);

                return null;
        }

        private WebElement tryCachedLocator(String locatorKey) {
                HealedLocatorCandidate cached = HealedLocatorStore.getCached(locatorKey);

                if (cached == null) {
                        return null;
                }

                WebElement element = tryCandidate(cached);

                if (element != null) {
                        return element;
                }

                logger.warn(
                                "[Healing] Cached locator no longer works: {}",
                                cached);

                return null;
        }

        private WebElement tryCandidate(
                        HealedLocatorCandidate candidate) {
                try {
                        LocatorType type = LocatorType.valueOf(
                                        candidate.getLocatorType());

                        By locator = LocatorBuilder.build(
                                        type,
                                        candidate.getLocatorValue());

                        WebDriverWait wait = new WebDriverWait(
                                        DriverManager.getDriver(),
                                        Duration.ofSeconds(RETRY_TIMEOUT_SECONDS));

                        return wait.until(
                                        ExpectedConditions.visibilityOfElementLocated(locator));

                } catch (Exception e) {
                        logger.debug(
                                        "[Healing] Candidate failed: {} - {}",
                                        candidate,
                                        e.getClass().getSimpleName());

                        return null;
                }
        }

        private String capturePageSource() {
                try {
                        return DriverManager.getDriver().getPageSource();
                } catch (Exception e) {
                        logger.warn(
                                        "[Healing] Could not capture page source: {}",
                                        e.getMessage());
                        return "";
                }
        }
}