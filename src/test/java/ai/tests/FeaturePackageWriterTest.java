package ai.tests;

import core.ai.generation.AutomationFeaturePackage;
import core.ai.generation.AutomationProject;
import core.ai.generation.FeaturePackageWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class FeaturePackageWriterTest {

    @Test
    public void verifyFeaturePackageIsGenerated()
            throws Exception {

        Path outputDirectory =
                Files.createTempDirectory(
                        "mapaf-feature"
                );

        AutomationProject project =
                new AutomationProject(
                        "public class LoginScreen {}",
                        "public class LoginFlow {}",
                        "public class LoginTest {}",
                        "User successfully logs in",
                        """
                        TEST_USERNAME
                        TEST_PASSWORD
                        """,
                        """
                        Replace placeholder locators
                        Add real assertions
                        """
                );

        AutomationFeaturePackage featurePackage =
                new AutomationFeaturePackage(
                        "LoginFeature",
                        project
                );

        FeaturePackageWriter writer =
                new FeaturePackageWriter();

        writer.write(
                featurePackage,
                outputDirectory.toString()
        );

        Path featureDirectory =
                outputDirectory.resolve(
                        "LoginFeature"
                );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("GeneratedScreen.java")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("GeneratedFlow.java")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("GeneratedTest.java")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("ASSERTIONS.md")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("TEST_DATA.md")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("TODO_ITEMS.md")
                )
        );

        Assert.assertTrue(
                Files.exists(
                        featureDirectory
                                .resolve("README.md")
                )
        );
    }
}
