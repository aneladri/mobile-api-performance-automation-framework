package web.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.locators.LocatorRepository;
import web.locators.WebLocator;

public class LocatorRepositoryTest {

    @Test
    public void shouldRegisterAndRetrieveLocator() {
        LocatorRepository repository =
                new LocatorRepository();

        WebLocator locator =
                WebLocator.id(
                        "Username Input",
                        "username"
                );

        repository.register(locator);

        Assert.assertEquals(
                repository.size(),
                1
        );

        Assert.assertSame(
                repository.get("Username Input"),
                locator
        );
    }

    @Test
    public void shouldIgnoreLocatorNameCase() {
        LocatorRepository repository =
                new LocatorRepository();

        repository.register(
                WebLocator.id(
                        "Username Input",
                        "username"
                )
        );

        Assert.assertTrue(
                repository.contains(
                        "username input"
                )
        );

        Assert.assertNotNull(
                repository.get(
                        "USERNAME INPUT"
                )
        );
    }

    @Test
    public void shouldProtectRepositoryCollection() {
        LocatorRepository repository =
                new LocatorRepository();

        repository.register(
                WebLocator.id(
                        "Username",
                        "username"
                )
        );

        repository.getAll().clear();

        Assert.assertEquals(
                repository.size(),
                1
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator already registered: Username"
    )
    public void shouldRejectDuplicateLocator() {
        LocatorRepository repository =
                new LocatorRepository();

        repository.register(
                WebLocator.id(
                        "Username",
                        "username"
                )
        );

        repository.register(
                WebLocator.css(
                        "username",
                        "#other-username"
                )
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator not found: Missing Locator"
    )
    public void shouldRejectMissingLocator() {
        new LocatorRepository().get(
                "Missing Locator"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Web locator must not be null"
    )
    public void shouldRejectNullLocator() {
        new LocatorRepository().register(null);
    }
}
