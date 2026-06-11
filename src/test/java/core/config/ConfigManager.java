package core.config;

import core.utils.PropertyLoader;

import java.util.Properties;

public class ConfigManager {

    private static final String DEFAULT_ENV = "qa";
    private static final Properties properties;

    static {
        String env = System.getProperty("env", DEFAULT_ENV);
        properties = PropertyLoader.load("env/" + env + ".properties");
    }

    private ConfigManager() {
    }

    public static String get(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

    public static String getRequired(String key) {
        String value = get(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException("Missing required configuration key: " + key);
        }

        return value;
    }

    public static String getEnvironment() {
        return System.getProperty("env", DEFAULT_ENV);
    }
}