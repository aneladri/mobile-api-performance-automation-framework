package roomscan.screens;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import roomscan.locators.RoomScanLocators;

public class RoomUploadScreen extends HealingBaseScreen {

    private final By uploadProgress =
            AppiumBy.accessibilityId(
                    RoomScanLocators.UPLOAD_PROGRESS
            );

    private final By uploadSuccess =
            AppiumBy.accessibilityId(
                    RoomScanLocators.UPLOAD_SUCCESS
            );

    private final By uploadFailure =
            AppiumBy.accessibilityId(
                    RoomScanLocators.UPLOAD_FAILURE
            );

    private final By retryUploadButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.RETRY_UPLOAD_BUTTON
            );

    private final By viewHistoryButton =
            AppiumBy.accessibilityId(
                    RoomScanLocators.VIEW_HISTORY_BUTTON
            );

    public boolean isUploading() {
        return isDisplayed(uploadProgress);
    }

    public boolean isUploadSuccessful() {
        return isDisplayed(uploadSuccess);
    }

    public boolean isUploadFailed() {
        return isDisplayed(uploadFailure);
    }

    public void retryUpload() {
        tap(retryUploadButton);
    }

    public void openHistory() {
        tap(viewHistoryButton);
    }
}
