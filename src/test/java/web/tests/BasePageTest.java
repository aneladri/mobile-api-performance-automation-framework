package web.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import web.pages.TestFormPage;
import web.tests.base.BaseWebTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BasePageTest extends BaseWebTest {

    private final ThreadLocal<TestFormPage> formPageHolder =
            new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void loadTestPage() {
        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>MAPAF Base Page Test</title>
                  </head>
                  <body>
                    <input id="name" type="text">

                    <input
                      id="email"
                      name="email"
                      type="email"
                      placeholder="name@example.com">

                    <input
                      id="active"
                      type="checkbox">

                    <select id="country">
                      <option value="">Choose</option>
                      <option value="IN">India</option>
                      <option value="US">United States</option>
                    </select>

                    <input id="file" type="file">

                    <button
                      id="submit"
                      onclick="
                        document.getElementById('result')
                          .textContent =
                          document.getElementById('name').value;
                      ">
                      Submit
                    </button>

                    <div id="result"></div>

                    <ul>
                      <li class="item">API</li>
                      <li class="item">Mobile</li>
                      <li class="item">Performance</li>
                    </ul>
                  </body>
                </html>
                """
        );

        formPageHolder.set(
                new TestFormPage(page())
        );
    }

    @AfterMethod(alwaysRun = true)
    public void clearTestPage() {
        formPageHolder.remove();
    }

    @Test
    public void shouldFillAndReadInputValue() {
        formPage().enterName("MAPAF");

        Assert.assertEquals(
                formPage().getNameValue(),
                "MAPAF"
        );

        formPage().clearName();

        Assert.assertEquals(
                formPage().getNameValue(),
                ""
        );
    }

    @Test
    public void shouldCheckAndUncheckCheckbox() {
        formPage().setActive(true);

        Assert.assertTrue(
                formPage().isActive()
        );

        formPage().setActive(false);

        Assert.assertFalse(
                formPage().isActive()
        );
    }

    @Test
    public void shouldSelectDropdownOption() {
        formPage().selectCountryByValue("IN");

        Assert.assertEquals(
                page()
                        .locator("#country")
                        .inputValue(),
                "IN"
        );

        formPage().selectCountryByLabel(
                "United States"
        );

        Assert.assertEquals(
                page()
                        .locator("#country")
                        .inputValue(),
                "US"
        );
    }

    @Test
    public void shouldClickButtonAndReadText() {
        formPage().enterName(
                "Web Automation Engine"
        );

        formPage().submit();

        Assert.assertEquals(
                formPage().getResult(),
                "Web Automation Engine"
        );
    }

    @Test
    public void shouldReadAttributesAndElementLists() {
        Assert.assertEquals(
                formPage().getEmailPlaceholder(),
                "name@example.com"
        );

        Assert.assertTrue(
                formPage().isSubmitEnabled()
        );

        Assert.assertEquals(
                formPage().getItemCount(),
                3
        );

        Assert.assertEquals(
                formPage().getItems(),
                List.of(
                        "API",
                        "Mobile",
                        "Performance"
                )
        );
    }

    @Test
    public void shouldUploadFile()
            throws Exception {

        Path file = Files.createTempFile(
                "mapaf-upload",
                ".txt"
        );

        try {
            Files.writeString(
                    file,
                    "MAPAF upload test",
                    StandardCharsets.UTF_8
            );

            formPage().upload(file);

            Assert.assertEquals(
                    page()
                            .locator("#file")
                            .evaluate(
                                    "element => "
                                            + "element.files.length === 1"
                            ),
                    true
            );

            Assert.assertEquals(
                    page()
                            .locator("#file")
                            .evaluate(
                                    "element => "
                                            + "element.files[0].name"
                            )
                            .toString(),
                    file.getFileName().toString()
            );
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Playwright page must not be null"
    )
    public void shouldRejectNullPage() {
        new TestFormPage(null);
    }

    private TestFormPage formPage() {
        TestFormPage formPage =
                formPageHolder.get();

        if (formPage == null) {
            throw new IllegalStateException(
                    "Test form page is not initialized "
                            + "for the current thread"
            );
        }

        return formPage;
    }
}