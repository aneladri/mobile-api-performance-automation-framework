package web.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.config.WebConfiguration;
import web.enums.BrowserType;
import web.enums.ExecutionMode;

public class WebConfigurationTest {

        @Test
        public void shouldCreateDefaultConfiguration() {

                WebConfiguration configuration = WebConfiguration.defaultConfiguration();

                Assert.assertEquals(
                                configuration.getBrowser(),
                                BrowserType.CHROMIUM);

                Assert.assertEquals(
                                configuration.getExecutionMode(),
                                ExecutionMode.LOCAL);

                Assert.assertTrue(
                                configuration.isHeadless());

                Assert.assertEquals(
                                configuration.getViewportWidth(),
                                1440);

                Assert.assertEquals(
                                configuration.getViewportHeight(),
                                900);

                Assert.assertEquals(
                                configuration.getTimeoutSeconds(),
                                30);

                Assert.assertTrue(
                                configuration.isScreenshotsEnabled());

                Assert.assertTrue(
                                configuration.isTraceEnabled());

                Assert.assertTrue(
                                configuration.isVideoEnabled());

                Assert.assertTrue(
                                configuration.isConsoleLogsEnabled());

                Assert.assertEquals(
                                configuration
                                                .getArtifactDirectory()
                                                .toString(),
                                "web/artifacts");
        }

        @Test
        public void shouldUpdateConfiguration() {

                WebConfiguration configuration = new WebConfiguration();

                configuration.setBrowser(
                                BrowserType.FIREFOX);

                configuration.setExecutionMode(
                                ExecutionMode.REMOTE);

                configuration.setHeadless(false);

                configuration.setViewportWidth(1920);

                configuration.setViewportHeight(1080);

                configuration.setTimeoutSeconds(60);

                Assert.assertEquals(
                                configuration.getBrowser(),
                                BrowserType.FIREFOX);

                Assert.assertEquals(
                                configuration.getExecutionMode(),
                                ExecutionMode.REMOTE);

                Assert.assertFalse(
                                configuration.isHeadless());
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldRejectNullBrowser() {

                new WebConfiguration()
                                .setBrowser(null);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldRejectNullExecutionMode() {

                new WebConfiguration()
                                .setExecutionMode(null);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldRejectInvalidViewportWidth() {

                new WebConfiguration()
                                .setViewportWidth(0);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldRejectInvalidViewportHeight() {

                new WebConfiguration()
                                .setViewportHeight(-1);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldRejectInvalidTimeout() {

                new WebConfiguration()
                                .setTimeoutSeconds(0);
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Artifact directory must not be null")
        public void shouldRejectNullArtifactDirectory() {
                new WebConfiguration()
                                .setArtifactDirectory(null);
        }
}
