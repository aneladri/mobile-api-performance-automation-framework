package common.reporting.tests;

import common.reporting.dashboard.publisher.DashboardPublisher;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DashboardPublisherTest {

    @Test
    public void shouldPublishDashboard()
            throws Exception {

        Path sourceDirectory =
                Files.createTempDirectory(
                        "mapaf-dashboard-source"
                );

        Path dashboardFile =
                sourceDirectory.resolve("index.html");

        Files.writeString(
                dashboardFile,
                "<html><body>MAPAF Dashboard</body></html>",
                StandardCharsets.UTF_8
        );

        Path publicationDirectory =
                Files.createTempDirectory(
                        "mapaf-dashboard-publication"
                );

        Path publishedFile =
                new DashboardPublisher().publish(
                        dashboardFile,
                        publicationDirectory
                );

        Assert.assertTrue(
                Files.exists(publishedFile)
        );

        Assert.assertEquals(
                publishedFile.getFileName().toString(),
                "index.html"
        );

        Assert.assertEquals(
                Files.readString(
                        publishedFile,
                        StandardCharsets.UTF_8
                ),
                "<html><body>MAPAF Dashboard</body></html>"
        );
    }

    @Test
    public void shouldPublishUsingCustomFilename()
            throws Exception {

        Path dashboardFile =
                createDashboardFile(
                        "custom-dashboard-source"
                );

        Path publicationDirectory =
                Files.createTempDirectory(
                        "mapaf-custom-publication"
                );

        Path publishedFile =
                new DashboardPublisher().publish(
                        dashboardFile,
                        publicationDirectory,
                        "quality-report.html"
                );

        Assert.assertEquals(
                publishedFile.getFileName().toString(),
                "quality-report.html"
        );

        Assert.assertTrue(
                Files.exists(publishedFile)
        );
    }

    @Test
    public void shouldCreateNestedPublicationDirectory()
            throws Exception {

        Path dashboardFile =
                createDashboardFile(
                        "nested-dashboard-source"
                );

        Path rootDirectory =
                Files.createTempDirectory(
                        "mapaf-publication-root"
                );

        Path publicationDirectory =
                rootDirectory.resolve(
                        "published/reports/dashboard"
                );

        Path publishedFile =
                new DashboardPublisher().publish(
                        dashboardFile,
                        publicationDirectory
                );

        Assert.assertTrue(
                Files.exists(publicationDirectory)
        );

        Assert.assertTrue(
                Files.exists(publishedFile)
        );
    }

    @Test
    public void shouldReplaceExistingPublishedDashboard()
            throws Exception {

        Path dashboardFile =
                createDashboardFile(
                        "replacement-dashboard-source"
                );

        Path publicationDirectory =
                Files.createTempDirectory(
                        "mapaf-replacement-publication"
                );

        Path existingFile =
                publicationDirectory.resolve(
                        "index.html"
                );

        Files.writeString(
                existingFile,
                "Old dashboard",
                StandardCharsets.UTF_8
        );

        Path publishedFile =
                new DashboardPublisher().publish(
                        dashboardFile,
                        publicationDirectory
                );

        Assert.assertEquals(
                Files.readString(
                        publishedFile,
                        StandardCharsets.UTF_8
                ),
                "New dashboard"
        );
    }

    @Test
    public void shouldReturnSourceWhenPublishingToSameLocation()
            throws Exception {

        Path dashboardFile =
                createDashboardFile(
                        "same-location-dashboard"
                );

        Path publishedFile =
                new DashboardPublisher().publish(
                        dashboardFile,
                        dashboardFile.getParent()
                );

        Assert.assertEquals(
                publishedFile,
                dashboardFile.toAbsolutePath().normalize()
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard file must not be null"
    )
    public void shouldRejectNullDashboardFile()
            throws Exception {

        new DashboardPublisher().publish(
                null,
                Files.createTempDirectory(
                        "mapaf-null-dashboard"
                )
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard file does not exist: .*"
    )
    public void shouldRejectMissingDashboardFile()
            throws Exception {

        Path directory =
                Files.createTempDirectory(
                        "mapaf-missing-dashboard"
                );

        new DashboardPublisher().publish(
                directory.resolve("missing.html"),
                directory.resolve("published")
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard publication directory must not be null"
    )
    public void shouldRejectNullPublicationDirectory()
            throws Exception {

        new DashboardPublisher().publish(
                createDashboardFile(
                        "null-publication-directory"
                ),
                null
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Published dashboard filename must not be blank"
    )
    public void shouldRejectBlankPublishedFilename()
            throws Exception {

        new DashboardPublisher().publish(
                createDashboardFile(
                        "blank-publication-filename"
                ),
                Files.createTempDirectory(
                        "mapaf-blank-filename"
                ),
                " "
        );
    }

    private Path createDashboardFile(
            String temporaryDirectoryPrefix
    ) throws Exception {

        Path directory =
                Files.createTempDirectory(
                        temporaryDirectoryPrefix
                );

        Path dashboardFile =
                directory.resolve("index.html");

        Files.writeString(
                dashboardFile,
                "New dashboard",
                StandardCharsets.UTF_8
        );

        return dashboardFile;
    }
}
