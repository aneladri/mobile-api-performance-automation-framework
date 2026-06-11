package core.config;

import core.utils.PropertyLoader;

import java.util.Properties;

public class ConfigManager {

    private static Properties properties;

    static {

        String env = System.getProperty("env", "qa");

        properties = PropertyLoader.load(
                "env/" + env + ".properties"
        );
    }

    private ConfigManager() {
    }

    public static String get(String key) {

        return System.getProperty(
                key,
                properties.getProperty(key)
        );
    }

    public static String getEnvironment() {

        return System.getProperty(
                "env",
                "qa"
        );
    }
}