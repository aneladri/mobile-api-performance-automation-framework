package ai.tests;

import core.ai.generation.AutomationProject;
import core.ai.generation.JavaSourceWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class JavaSourceWriterTest {

    @Test
    public void verifyJavaSourceWriterCreatesGeneratedFiles()
            throws Exception {

        Path outputDirectory =
                Files.createTempDirectory("mapaf-generated");

        AutomationProject project =
                new AutomationProject(
                        "package generated.test;\npublic class GeneratedScreen {}",
                        "package generated.test;\npublic class GeneratedFlow {}",
                        "package generated.test;\npublic class GeneratedTest {}",
                        "Generated assertions",
                        "Generated test data",
                        "Generated TODO items"
                );

        JavaSourceWriter writer =
                new JavaSourceWriter();

        writer.write(
                project,
                outputDirectory.toString()
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("GeneratedScreen.java"))
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("GeneratedFlow.java"))
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("GeneratedTest.java"))
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("ASSERTIONS.md"))
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("TEST_DATA.md"))
        );

        Assert.assertTrue(
                Files.exists(outputDirectory.resolve("TODO_ITEMS.md"))
        );
    }


    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    ".*truncated or incomplete.*"
    )
    public void shouldRejectTruncatedJavaSource() throws Exception {
        Path outputDirectory =
                Files.createTempDirectory("mapaf-truncated");

        AutomationProject project = new AutomationProject(
                "package generated.test;\npublic class BrokenScreen {",
                "",
                "",
                "",
                "",
                ""
        );

        new JavaSourceWriter().write(
                project,
                outputDirectory.toString()
        );
    }
}
