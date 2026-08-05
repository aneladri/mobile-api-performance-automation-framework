package core.ai.persistence;

import java.nio.file.Path;

public final class StorePaths {

    public static final Path HEALING_DIRECTORY =
            Path.of(".healing-store");

    public static final Path HEALED_LOCATORS =
            HEALING_DIRECTORY.resolve(
                    "healed-locators.json"
            );

    public static final Path STRATEGY_LEARNING =
            HEALING_DIRECTORY.resolve(
                    "strategy-learning.json"
            );

    private StorePaths() {
    }
}
