package web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import web.locators.WebLocator;

import java.nio.file.Path;
import java.util.List;

public abstract class BasePage {

    private final Page page;

    protected BasePage(Page page) {
        if (page == null) {
            throw new IllegalArgumentException(
                    "Playwright page must not be null");
        }

        this.page = page;
    }

    protected Page page() {
        return page;
    }

    protected Locator locator(String selector) {
        validateSelector(selector);

        return page.locator(selector);
    }

    protected Locator locator(
            WebLocator webLocator) {
        if (webLocator == null) {
            throw new IllegalArgumentException(
                    "Web locator must not be null");
        }

        return page.locator(
                webLocator.toSelector());
    }

    public void navigate(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "Navigation URL must not be blank");
        }

        page.navigate(url);
    }

    public String getTitle() {
        return page.title();
    }

    public String getUrl() {
        return page.url();
    }

    protected void click(String selector) {
        locator(selector).click();
    }

    protected void doubleClick(String selector) {
        locator(selector).dblclick();
    }

    protected void fill(
            String selector,
            String value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Input value must not be null");
        }

        locator(selector).fill(value);
    }

    protected void clear(String selector) {
        locator(selector).clear();
    }

    protected void press(
            String selector,
            String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Keyboard key must not be blank");
        }

        locator(selector).press(key);
    }

    protected void check(String selector) {
        locator(selector).check();
    }

    protected void uncheck(String selector) {
        locator(selector).uncheck();
    }

    protected boolean isChecked(String selector) {
        return locator(selector).isChecked();
    }

    protected boolean isVisible(String selector) {
        return locator(selector).isVisible();
    }

    protected boolean isEnabled(String selector) {
        return locator(selector).isEnabled();
    }

    protected boolean isEditable(String selector) {
        return locator(selector).isEditable();
    }

    protected String text(String selector) {
        return locator(selector).textContent();
    }

    protected String innerText(String selector) {
        return locator(selector).innerText();
    }

    protected String inputValue(String selector) {
        return locator(selector).inputValue();
    }

    protected String attribute(
            String selector,
            String attributeName) {
        if (attributeName == null
                || attributeName.isBlank()) {

            throw new IllegalArgumentException(
                    "Attribute name must not be blank");
        }

        return locator(selector).getAttribute(
                attributeName);
    }

    protected int count(String selector) {
        return locator(selector).count();
    }

    protected List<String> allTextContents(
            String selector) {
        return locator(selector).allTextContents();
    }

    protected void selectByValue(
            String selector,
            String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Select option value must not be blank");
        }

        locator(selector).selectOption(value);
    }

    protected void selectByLabel(
            String selector,
            String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException(
                    "Select option label must not be blank");
        }

        locator(selector).selectOption(
                new SelectOption().setLabel(label));
    }

    protected void selectByIndex(
            String selector,
            int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "Select option index must not be negative");
        }

        locator(selector).selectOption(
                new SelectOption().setIndex(index));
    }

    protected void uploadFile(
            String selector,
            Path file) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "Upload file must not be null");
        }

        locator(selector).setInputFiles(file);
    }

    protected void hover(String selector) {
        locator(selector).hover();
    }

    protected void scrollIntoView(
            String selector) {
        locator(selector).scrollIntoViewIfNeeded();
    }

    protected void dragAndDrop(
            String sourceSelector,
            String targetSelector) {
        Locator source = locator(sourceSelector);
        Locator target = locator(targetSelector);

        source.dragTo(target);
    }

    protected void focus(String selector) {
        locator(selector).focus();
    }

    protected void waitForVisible(
            String selector) {
        locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(
                                com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    private void validateSelector(
            String selector) {
        if (selector == null || selector.isBlank()) {
            throw new IllegalArgumentException(
                    "Locator selector must not be blank");
        }
    }

    protected void click(
            WebLocator webLocator) {
        locator(webLocator).click();
    }

    protected void fill(
            WebLocator webLocator,
            String value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Input value must not be null");
        }

        locator(webLocator).fill(value);
    }

    protected void clear(
            WebLocator webLocator) {
        locator(webLocator).clear();
    }

    protected void check(
            WebLocator webLocator) {
        locator(webLocator).check();
    }

    protected void uncheck(
            WebLocator webLocator) {
        locator(webLocator).uncheck();
    }

    protected boolean isChecked(
            WebLocator webLocator) {
        return locator(webLocator).isChecked();
    }

    protected boolean isVisible(
            WebLocator webLocator) {
        return locator(webLocator).isVisible();
    }

    protected boolean isEnabled(
            WebLocator webLocator) {
        return locator(webLocator).isEnabled();
    }

    protected String innerText(
            WebLocator webLocator) {
        return locator(webLocator).innerText();
    }

    protected String inputValue(
            WebLocator webLocator) {
        return locator(webLocator).inputValue();
    }

    protected String attribute(
            WebLocator webLocator,
            String attributeName) {
        if (attributeName == null
                || attributeName.isBlank()) {

            throw new IllegalArgumentException(
                    "Attribute name must not be blank");
        }

        return locator(webLocator)
                .getAttribute(attributeName);
    }

    protected int count(
            WebLocator webLocator) {
        return locator(webLocator).count();
    }

    protected List<String> allTextContents(
            WebLocator webLocator) {
        return locator(webLocator)
                .allTextContents();
    }

    protected void selectByValue(
            WebLocator webLocator,
            String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Select option value must not be blank");
        }

        locator(webLocator).selectOption(value);
    }

    protected void uploadFile(
            WebLocator webLocator,
            Path file) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "Upload file must not be null");
        }

        locator(webLocator).setInputFiles(file);
    }

    protected void selectByLabel(
            WebLocator webLocator,
            String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException(
                    "Select option label must not be blank");
        }

        locator(webLocator).selectOption(
                new SelectOption().setLabel(label));
    }
}
