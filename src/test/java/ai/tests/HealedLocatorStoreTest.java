package ai.tests;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.healing.HealedLocatorRecord;
import core.ai.healing.HealedLocatorStore;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class HealedLocatorStoreTest {

    private Path temporaryDirectory;
    private Path storeFile;

    @BeforeMethod
    public void setUp()
            throws Exception {

        temporaryDirectory =
                Files.createTempDirectory(
                        "healed-locator-store-test"
                );

        storeFile =
                temporaryDirectory.resolve(
                        "healed-locators.json"
                );

        System.setProperty(
                "ai.healing.store.path",
                storeFile.toString()
        );

        HealedLocatorStore.clear();
        HealedLocatorStore.reload();
    }

    @AfterMethod
    public void tearDown()
            throws Exception {

        HealedLocatorStore.clear();

        System.clearProperty(
                "ai.healing.store.path"
        );

        if (Files.exists(temporaryDirectory)) {

            try (var paths =
                         Files.walk(
                                 temporaryDirectory
                         )) {

                paths.sorted(
                                (left, right) ->
                                        right.compareTo(left)
                        )
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception ignored) {
                                // Test cleanup only.
                            }
                        });
            }
        }
    }

    @Test
    public void shouldStoreAndReturnCandidate() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        HealedLocatorCandidate cached =
                HealedLocatorStore.getCached(
                        "By.id: old-submit"
                );

        Assert.assertNotNull(cached);

        Assert.assertEquals(
                cached.getLocatorType(),
                "ACCESSIBILITY_ID"
        );

        Assert.assertEquals(
                cached.getLocatorValue(),
                "submit-order"
        );

        Assert.assertTrue(
                Files.exists(storeFile)
        );
    }

    @Test
    public void shouldPersistAndReloadRecord() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        Assert.assertEquals(
                HealedLocatorStore.size(),
                1
        );

        HealedLocatorStore.reload();

        HealedLocatorRecord record =
                HealedLocatorStore.getRecord(
                        "By.id: old-submit"
                );

        Assert.assertNotNull(record);

        Assert.assertEquals(
                record.getScreen(),
                "CheckoutScreen"
        );

        Assert.assertEquals(
                record.getTestName(),
                "submitOrderTest"
        );

        Assert.assertEquals(
                record.getSuccessCount(),
                1
        );
    }

    @Test
    public void shouldRecordSuccessfulReuse() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        HealedLocatorStore.recordSuccess(
                "By.id: old-submit"
        );

        HealedLocatorRecord record =
                HealedLocatorStore.getRecord(
                        "By.id: old-submit"
                );

        Assert.assertEquals(
                record.getSuccessCount(),
                2
        );

        Assert.assertEquals(
                record.getFailureCount(),
                0
        );
    }

    @Test
    public void shouldRecordFailedReuse() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        HealedLocatorStore.recordFailure(
                "By.id: old-submit"
        );

        HealedLocatorRecord record =
                HealedLocatorStore.getRecord(
                        "By.id: old-submit"
                );

        Assert.assertEquals(
                record.getFailureCount(),
                1
        );
    }

    @Test
    public void shouldRemovePersistentRecord() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        HealedLocatorStore.remove(
                "By.id: old-submit"
        );

        Assert.assertFalse(
                HealedLocatorStore.has(
                        "By.id: old-submit"
                )
        );

        HealedLocatorStore.reload();

        Assert.assertFalse(
                HealedLocatorStore.has(
                        "By.id: old-submit"
                )
        );
    }

    @Test
    public void shouldClearMemoryAndPersistentFile() {

        HealedLocatorStore.store(
                "By.id: old-submit",
                candidate(),
                "CheckoutScreen",
                "submitOrderTest"
        );

        Assert.assertTrue(
                Files.exists(storeFile)
        );

        HealedLocatorStore.clear();

        Assert.assertEquals(
                HealedLocatorStore.size(),
                0
        );

        Assert.assertFalse(
                Files.exists(storeFile)
        );
    }

    @Test
    public void shouldIgnoreMissingRecordUpdates() {

        HealedLocatorStore.recordSuccess(
                "By.id: missing"
        );

        HealedLocatorStore.recordFailure(
                "By.id: missing"
        );

        Assert.assertEquals(
                HealedLocatorStore.size(),
                0
        );
    }

    @Test
    public void shouldRecoverFromMalformedStoreFile()
            throws Exception {

        Files.createDirectories(
                storeFile.getParent()
        );

        Files.writeString(
                storeFile,
                "{not-valid-json"
        );

        HealedLocatorStore.reload();

        Assert.assertEquals(
                HealedLocatorStore.size(),
                0
        );
    }

    private HealedLocatorCandidate candidate() {

        return new HealedLocatorCandidate(
                "ACCESSIBILITY_ID",
                "submit-order",
                91,
                "Stable accessibility identifier"
        );
    }
}
