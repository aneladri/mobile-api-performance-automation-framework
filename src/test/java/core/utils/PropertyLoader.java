package core.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    private PropertyLoader() {
    }

    public static Properties load(String filePath) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties file: " + filePath, e);
        }

        return properties;
    }
}