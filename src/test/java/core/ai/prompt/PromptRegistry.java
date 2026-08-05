package core.ai.prompt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for prompt definitions identified by name and version.
 */
public final class PromptRegistry {

    private final Map<PromptKey, PromptDefinition> prompts =
            new LinkedHashMap<>();

    public synchronized void register(
            PromptDefinition definition) {

        Objects.requireNonNull(
                definition,
                "Prompt definition must not be null"
        );

        PromptKey key =
                PromptKey.from(definition);

        if (prompts.containsKey(key)) {
            throw new PromptException(
                    "Prompt is already registered: "
                            + key
            );
        }

        prompts.put(
                key,
                definition
        );
    }

    public synchronized PromptDefinition get(
            String name,
            String version) {

        PromptKey key =
                PromptKey.of(
                        name,
                        version
                );

        PromptDefinition definition =
                prompts.get(key);

        if (definition == null) {
            throw new PromptException(
                    "Prompt is not registered: "
                            + key
            );
        }

        return definition;
    }

    public synchronized PromptDefinition getLatest(
            String name) {

        String normalisedName =
                requireText(
                        name,
                        "Prompt name"
                );

        return prompts.entrySet()
                .stream()
                .filter(entry ->
                        entry.getKey()
                                .getName()
                                .equals(normalisedName)
                )
                .max(
                        Comparator.comparing(
                                entry ->
                                        entry.getKey()
                                                .getVersion(),
                                PromptRegistry::compareVersions
                        )
                )
                .map(Map.Entry::getValue)
                .orElseThrow(() ->
                        new PromptException(
                                "No registered prompt found for name: "
                                        + normalisedName
                        )
                );
    }

    public synchronized boolean contains(
            String name,
            String version) {

        return prompts.containsKey(
                PromptKey.of(
                        name,
                        version
                )
        );
    }

    public synchronized int size() {
        return prompts.size();
    }

    public synchronized List<PromptKey> listKeys() {

        return Collections.unmodifiableList(
                new ArrayList<>(
                        prompts.keySet()
                )
        );
    }

    private static int compareVersions(
            String left,
            String right) {

        String[] leftParts =
                normaliseVersion(left).split("\\.");

        String[] rightParts =
                normaliseVersion(right).split("\\.");

        int maximumLength =
                Math.max(
                        leftParts.length,
                        rightParts.length
                );

        for (int index = 0;
             index < maximumLength;
             index++) {

            int leftValue =
                    index < leftParts.length
                            ? parseVersionPart(
                                    leftParts[index]
                            )
                            : 0;

            int rightValue =
                    index < rightParts.length
                            ? parseVersionPart(
                                    rightParts[index]
                            )
                            : 0;

            int comparison =
                    Integer.compare(
                            leftValue,
                            rightValue
                    );

            if (comparison != 0) {
                return comparison;
            }
        }

        return left.compareTo(right);
    }

    private static String normaliseVersion(
            String version) {

        String value =
                version.trim();

        if (value.startsWith("v")
                || value.startsWith("V")) {

            return value.substring(1);
        }

        return value;
    }

    private static int parseVersionPart(
            String value) {

        try {
            return Integer.parseInt(value);

        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static String requireText(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }
}
