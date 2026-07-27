package core.ai.prompt;

import java.util.Objects;

/**
 * Immutable identifier for a prompt name and version.
 */
public final class PromptKey {

    private final String name;
    private final String version;

    private PromptKey(
            String name,
            String version) {

        this.name = requireText(
                name,
                "Prompt name"
        );

        this.version = requireText(
                version,
                "Prompt version"
        );
    }

    public static PromptKey of(
            String name,
            String version) {

        return new PromptKey(
                name,
                version
        );
    }

    public static PromptKey from(
            PromptDefinition definition) {

        Objects.requireNonNull(
                definition,
                "Prompt definition must not be null"
        );

        return of(
                definition.getName(),
                definition.getVersion()
        );
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
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

    @Override
    public boolean equals(Object value) {

        if (this == value) {
            return true;
        }

        if (!(value instanceof PromptKey other)) {
            return false;
        }

        return name.equals(other.name)
                && version.equals(other.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public String toString() {
        return name + ":" + version;
    }
}
