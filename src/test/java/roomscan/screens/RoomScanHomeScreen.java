package roomscan.screens;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import roomscan.locators.RoomScanLocators;

public class RoomScanHomeScreen extends HealingBaseScreen {

    private final By homeScreenTitle =
            AppiumBy.accessibilityId(
                    RoomScanLocators.HOME_SCREEN_TITLE
            );

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

    /**
     * Verifies the Room Scan home screen is displayed.
     */
    public boolean isLoaded() {
        return isDisplayed(
                homeScreenTitle
        );
    }

    /**
     * Verifies the Start Scan button is available.
     */
    public boolean isReadyToScan() {
        return isDisplayed(
                startScanButton
        );
    }

    /**
     * Starts a new room scan.
     */
    public void startScan() {
        tap(
                startScanButton
        );
    }

    /**
     * Opens Scan History.
     */
    public void openScanHistory() {
        tap(
                scanHistoryButton
        );
    }

    /**
     * Opens Settings.
     */
    public void openSettings() {
        tap(
                settingsButton
        );
    }
}