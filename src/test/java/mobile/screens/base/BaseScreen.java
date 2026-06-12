package mobile.screens.base;

import core.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import mobile.utils.WaitUtils;

public class BaseScreen {

    protected AppiumDriver driver;

    public BaseScreen() {
        this.driver = DriverManager.getDriver();
    }

    protected WebElement find(By locator) {
        return WaitUtils.waitForVisible(locator);
    }

    protected void tap(By locator) {
        find(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement element = find(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected String getText(By locator) {
        return find(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        return find(locator).isDisplayed();
    }
}
