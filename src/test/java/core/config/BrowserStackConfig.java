package core.config;

public final class BrowserStackConfig {

    private BrowserStackConfig() {
    }

    public static String getHubUrl() {
        return String.format(
                "https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                getValue("BROWSERSTACK_USERNAME", "BROWSERSTACK_USERNAME"),
                getValue("BROWSERSTACK_ACCESS_KEY", "Test*123$")
        );
    }

    private static String getValue(String envKey, String propertyKey) {
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return ConfigManager.getRequired(propertyKey);
    }
}