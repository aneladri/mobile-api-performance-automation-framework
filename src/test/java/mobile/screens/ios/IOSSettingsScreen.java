package mobile.screens.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IOSSettingsScreen {

    private final AppiumDriver driver;

    private final By searchField =
            AppiumBy.iOSClassChain("**/XCUIElementTypeSearchField");

    private final By generalOption =
            AppiumBy.accessibilityId("General");

    public IOSSettingsScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return isSearchFieldVisible();
    }

    public boolean isSearchFieldVisible() {
        return !driver.findElements(searchField).isEmpty();
    }

    public void search(String text) {
        WebElement element = driver.findElement(searchField);

        System.out.println("Search field found");
        element.click();

        System.out.println("Search field clicked");
        element.clear();
        element.sendKeys(text);

        System.out.println("Entered text: " + text);
    }

    public String getSearchFieldValue() {
        return driver.findElement(searchField).getAttribute("value");
    }

    public boolean isGeneralVisible() {
        return !driver.findElements(generalOption).isEmpty();
    }

    public void tapGeneral() {
        driver.findElement(generalOption).click();
    }
}