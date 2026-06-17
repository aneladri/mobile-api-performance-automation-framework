package mobile.utils;

import core.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static String captureScreenshot(String testName) {
        try {
            File sourceFile = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String screenshotPath = "reports/screenshots/"
                    + testName
                    + "_"
                    + timestamp
                    + ".png";

            Files.createDirectories(Paths.get("reports/screenshots"));
            Files.copy(sourceFile.toPath(), Paths.get(screenshotPath));

            return screenshotPath;

        } catch (IOException e) {
            throw new RuntimeException("Failed to capture screenshot", e);
        }
    }
}
