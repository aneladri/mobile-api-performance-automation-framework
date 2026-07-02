package roomscan.screens;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import roomscan.locators.RoomScanLocators;

public class RoomScanHomeScreen extends HealingBaseScreen {

    private final By startScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.START_SCAN_BUTTON
            );

    private final By scanHistoryButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.SCAN_HISTORY_BUTTON
            );

    private final By settingsButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.SETTINGS_BUTTON
            );

    private final By homeScreenTitle =
            AppiumBy.accessibilityId(
                    RoomScanLocators.HOME_SCREEN_TITLE
            );

    public boolean isLoaded() {
        return isDisplayed(homeScreenTitle);
    }

    public boolean isReadyToScan() {
        return isDisplayed(startScanButton);
    }

    public void startScan() {
        click(startScanButton);
    }

    public void openScanHistory() {
        click(scanHistoryButton);
    }

    public void openSettings() {
        click(settingsButton);
    }
}