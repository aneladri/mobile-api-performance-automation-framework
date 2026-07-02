package roomscan.screens;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import roomscan.locators.RoomScanLocators;

public class RoomCaptureScreen extends HealingBaseScreen {

    private final By captureProgressIndicator =
            AppiumBy.accessibilityId(
                    RoomScanLocators.CAPTURE_PROGRESS_INDICATOR
            );

    private final By captureCoveragePercent =
            AppiumBy.accessibilityId(
                    RoomScanLocators.CAPTURE_COVERAGE_PERCENT
            );

    private final By pauseScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.PAUSE_SCAN_BUTTON
            );

    private final By resumeScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.RESUME_SCAN_BUTTON
            );

    private final By finishScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.FINISH_SCAN_BUTTON
            );

    private final By cancelScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.CANCEL_SCAN_BUTTON
            );

    private final By lowLightWarning =
            AppiumBy.accessibilityId(
                    RoomScanLocators.LOW_LIGHT_WARNING
            );

    public boolean isCaptureInProgress() {
        return isDisplayed(captureProgressIndicator);
    }

    public String getCoveragePercentText() {
        return getText(captureCoveragePercent);
    }

    public void pauseScan() {
        tap(pauseScanButton);
    }

    public void resumeScan() {
        tap(resumeScanButton);
    }

    public void finishScan() {
        tap(finishScanButton);
    }

    public void cancelScan() {
        tap(cancelScanButton);
    }

    public boolean isLowLightWarningVisible() {
        return isDisplayed(lowLightWarning);
    }
}