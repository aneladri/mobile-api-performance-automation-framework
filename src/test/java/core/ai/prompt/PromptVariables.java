package core.ai.prompt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class PromptVariables {

    private final Map<String, String> variables;

    private PromptVariables(Builder builder) {
        this.variables = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.variables)
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public String get(String key) {
        return variables.get(key);
    }

    public boolean contains(String key) {
        return variables.containsKey(key);
    }

    public int size() {
        return variables.size();
    }

    public Map<String, String> asMap() {
        return variables;
    }

    public static final class Builder {

        private final Map<String, String> variables =
                new LinkedHashMap<>();

        public Builder put(
                String key,
                String value) {

            Objects.requireNonNull(
                    key,
                    "Variable name must not be null"
            );

            variables.put(
                    key,
                    value == null ? "" : value
            );

            return this;
        }

        public Builder putAll(
                Map<String, String> values) {

            Objects.requireNonNull(values);

            values.forEach(this::put);

            return this;
        }

        public PromptVariables build() {
            return new PromptVariables(this);
        }
    }
}
