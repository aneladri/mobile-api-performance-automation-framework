package core.ai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class PromptLoader {

    private static final String PROMPT_BASE_PATH =
            "ai/prompts/";

    private static final Map<String, String> CACHE =
            new ConcurrentHashMap<>();

    private PromptLoader() {
    }

    public static String load(String promptName) {

        if (promptName == null || promptName.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt name cannot be null or blank"
            );
        }

        return CACHE.computeIfAbsent(
                promptName,
                PromptLoader::loadFromResources
        );
    }

    private static String loadFromResources(String promptName) {

        String resourcePath =
                PROMPT_BASE_PATH + promptName;

        InputStream inputStream =
                PromptLoader.class
                        .getClassLoader()
                        .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new RuntimeException(
                    "Prompt resource not found: " + resourcePath
            );
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            return reader
                    .lines()
                    .collect(
                            Collectors.joining(System.lineSeparator())
                    );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load prompt resource: " + resourcePath,
                    e
            );
        }
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
