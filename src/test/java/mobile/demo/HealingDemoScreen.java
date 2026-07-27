package mobile.demo;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Tier-3 (AI) self-healing demo screen.
 *
 * VIEWS_MENU_ITEM is intentionally broken: it targets an accessibility ID
 * that does not exist ("btn_views_menu_item"), simulating a locator that
 * broke after a rebuild renamed/removed a custom contentDescription. The
 * REAL element is still on screen — ApiDemos' main list row with
 * content-desc="Views" (confirmed from a live `adb shell uiautomator dump`
 * against apps/android/ApiDemos-release.apk).
 *
 * This locator shape is deliberate: because it's built via
 * AppiumBy.accessibilityId(...) rather than By.id(...) or By.xpath(...),
 * none of LocalHealingRuleEngine's four Tier-2 rules structurally apply to
 * it (tryIdSuffixVariants only fires for "By.id:"-prefixed locators,
 * tryClassIndex only for "By.xpath:"-prefixed locators, and the two
 * text/accessibility-scan rules match against the WHOLE extracted token
 * including its "Accessibility ID: " prefix, which never appears verbatim
 * in a real page source). So Tier 2 is guaranteed to genuinely miss, and
 * the request deterministically falls through to Tier 3 every run — this
 * is an honest gap in the current rule engine, not a rigged demo.
 */
public class HealingDemoScreen extends HealingBaseScreen {

    private static final By VIEWS_MENU_ITEM =
            AppiumBy.accessibilityId("btn_views_menu_item");

    /**
     * Tier-2 (local rule engine) demo locator: deliberately broken with a
     * fake, stale resource-id ("old_broken_resource_id_2024") combined with
     * the CORRECT, current visible text ("Views"). This simulates the most
     * common real-world locator break: a rebuild changes an internal
     * resource ID, but the on-screen label never changes. No single element
     * satisfies both conditions, so the initial lookup genuinely fails and
     * triggers healing - but LocalHealingRuleEngine's text-based XPath rule
     * (tryTextXPathFromSource) can recover it using the text alone, with
     * zero AI cost, because the extracted token ("Views") is a clean,
     * unescaped match against the raw locator's @text='...' clause and it
     * appears verbatim in the real page source.
     */
    private static final By VIEWS_MENU_ITEM_STALE_ID =
            By.xpath("//*[@text='Views' and @resource-id='old_broken_resource_id_2024']");

    public void tapViewsMenuItem() {
        tap(VIEWS_MENU_ITEM);
    }

    public void tapViewsMenuItemViaStaleResourceId() {
        tap(VIEWS_MENU_ITEM_STALE_ID);
    }
}
