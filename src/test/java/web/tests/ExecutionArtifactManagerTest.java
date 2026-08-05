package web.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.tests.base.BaseWebTest;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExecutionArtifactManagerTest
        extends BaseWebTest {

    @Test
    public void shouldCreateExecutionArtifactDirectory() {
        Path directory = artifactDirectory();

        Assert.assertNotNull(directory);
        Assert.assertTrue(
                Files.exists(directory)
        );

        Assert.assertTrue(
                Files.isDirectory(directory)
        );
    }

    @Test
    public void shouldCaptureBrowserConsoleMessages() {
        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <body>
                    <script>
                      console.log('MAPAF console message');
                      console.warn('MAPAF warning message');
                    </script>
                  </body>
                </html>
                """
        );

        page().waitForTimeout(100);

        Assert.assertTrue(
                artifacts()
                        .getConsoleEntries()
                        .stream()
                        .anyMatch(
                                value -> value.contains(
                                        "MAPAF console message"
                                )
                        )
        );

        Assert.assertTrue(
                artifacts()
                        .getConsoleEntries()
                        .stream()
                        .anyMatch(
                                value -> value.contains(
                                        "MAPAF warning message"
                                )
                        )
        );
    }

    @Test
    public void shouldCapturePageErrors() {
        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <body>
                    <script>
                      setTimeout(() => {
                        throw new Error(
                          'MAPAF deliberate page error'
                        );
                      }, 10);
                    </script>
                  </body>
                </html>
                """
        );

        page().waitForTimeout(200);

        Assert.assertTrue(
                artifacts()
                        .getPageErrors()
                        .stream()
                        .anyMatch(
                                value -> value.contains(
                                        "MAPAF deliberate page error"
                                )
                        )
        );
    }

    @Test
    public void shouldCaptureScreenshotManually() {
        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <body>
                    <h1>MAPAF Artifact Screenshot</h1>
                  </body>
                </html>
                """
        );

        Path screenshot =
                artifacts()
                        .captureFailureScreenshot(
                                page()
                        );

        Assert.assertNotNull(screenshot);

        Assert.assertTrue(
                Files.exists(screenshot)
        );

        Assert.assertTrue(
                screenshot
                        .getFileName()
                        .toString()
                        .endsWith(".png")
        );
    }
}
