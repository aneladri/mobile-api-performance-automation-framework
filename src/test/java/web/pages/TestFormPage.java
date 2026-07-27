package web.pages;

import com.microsoft.playwright.Page;
import web.locators.WebLocator;

import java.nio.file.Path;
import java.util.List;

public class TestFormPage extends BasePage {

    private static final WebLocator NAME_INPUT = WebLocator.id(
            "Name Input",
            "name");

    private static final WebLocator EMAIL_INPUT = WebLocator.name(
            "Email Input",
            "email");

    private static final WebLocator ACTIVE_CHECKBOX = WebLocator.id(
            "Active Checkbox",
            "active");

    private static final WebLocator COUNTRY_SELECT = WebLocator.css(
            "Country Select",
            "#country");

    private static final WebLocator SUBMIT_BUTTON = WebLocator.text(
            "Submit Button",
            "Submit");

    private static final WebLocator RESULT = WebLocator.id(
            "Result",
            "result");

    private static final WebLocator FILE_INPUT = WebLocator.id(
            "File Input",
            "file");

    private static final WebLocator ITEM = WebLocator.css(
            "Automation Items",
            ".item");

    public TestFormPage(Page page) {
        super(page);
    }

    public void enterName(String name) {
        fill(NAME_INPUT, name);
    }

    public void enterEmail(String email) {
        fill(EMAIL_INPUT, email);
    }

    public void clearName() {
        clear(NAME_INPUT);
    }

    public String getNameValue() {
        return inputValue(NAME_INPUT);
    }

    public void setActive(boolean active) {
        if (active) {
            check(ACTIVE_CHECKBOX);
        } else {
            uncheck(ACTIVE_CHECKBOX);
        }
    }

    public boolean isActive() {
        return isChecked(ACTIVE_CHECKBOX);
    }

    public void selectCountryByValue(
            String value) {
        selectByValue(
                COUNTRY_SELECT,
                value);
    }

    public void selectCountryByLabel(
            String label) {
        selectByLabel(
                COUNTRY_SELECT,
                label);
    }

    public void submit() {
        click(SUBMIT_BUTTON);
    }

    public String getResult() {
        return innerText(RESULT);
    }

    public boolean isSubmitEnabled() {
        return isEnabled(SUBMIT_BUTTON);
    }

    public void upload(Path path) {
        uploadFile(FILE_INPUT, path);
    }

    public List<String> getItems() {
        return allTextContents(ITEM);
    }

    public int getItemCount() {
        return count(ITEM);
    }

    public String getEmailPlaceholder() {
        return attribute(
                EMAIL_INPUT,
                "placeholder");
    }
}