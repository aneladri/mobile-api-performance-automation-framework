package core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class JsonUtils {

    private JsonUtils() {
    }

    public static String getJsonAsString(String filePath) {

        try {

            return Files.readString(
                    Paths.get(filePath)
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read JSON file: "
                            + filePath,
                    e
            );
        }
    }
}
