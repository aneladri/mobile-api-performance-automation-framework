package core.config;

public final class BrowserStackConfig {

    private BrowserStackConfig() {
    }

    public static String getHubUrl() {
        return String.format(
                "https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                ConfigManager.getRequired("browserstackUserName"),
                ConfigManager.getRequired("browserstackAccessKey")
        );
    }
}