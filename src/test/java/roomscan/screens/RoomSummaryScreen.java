package roomscan.screens;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import roomscan.locators.RoomScanLocators;

public class RoomSummaryScreen extends HealingBaseScreen {

    private final By summaryTitle =
            AppiumBy.accessibilityId(
                    RoomScanLocators.ROOM_SUMMARY_TITLE
            );

    private final By roomCount =
            AppiumBy.accessibilityId(
                    RoomScanLocators.ROOM_COUNT
            );

    private final By wallCount =
            AppiumBy.accessibilityId(
                    RoomScanLocators.WALL_COUNT
            );

    private final By scanQuality =
            AppiumBy.accessibilityId(
                    RoomScanLocators.SCAN_QUALITY
            );

    private final By uploadScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.UPLOAD_SCAN_BUTTON
            );

    private final By retryScanButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.RETRY_SCAN_BUTTON
            );

    public boolean isLoaded() {
        return isDisplayed(summaryTitle);
    }

    public String getRoomCount() {
        return getText(roomCount);
    }

    public String getWallCount() {
        return getText(wallCount);
    }

    public String getScanQuality() {
        return getText(scanQuality);
    }

    public void uploadScan() {
        tap(uploadScanButton);
    }

    public void retryScan() {
        tap(retryScanButton);
    }
}
