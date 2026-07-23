package web.pages;

import com.microsoft.playwright.Page;

import java.nio.file.Path;
import java.util.List;

public class TestFormPage extends BasePage {

    private static final String NAME_INPUT =
            "#name";

    private static final String EMAIL_INPUT =
            "#email";

    private static final String ACTIVE_CHECKBOX =
            "#active";

    private static final String COUNTRY_SELECT =
            "#country";

    private static final String SUBMIT_BUTTON =
            "#submit";

    private static final String RESULT =
            "#result";

    private static final String FILE_INPUT =
            "#file";

    private static final String ITEM =
            ".item";

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
            String value
    ) {
        selectByValue(COUNTRY_SELECT, value);
    }

    public void selectCountryByLabel(
            String label
    ) {
        selectByLabel(COUNTRY_SELECT, label);
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
                "placeholder"
        );
    }
}
