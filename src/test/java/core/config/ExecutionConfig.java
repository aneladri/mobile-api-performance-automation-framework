package core.config;

import core.enums.ExecutionMode;

public final class ExecutionConfig {

    private ExecutionConfig() {
    }

    public static ExecutionMode getExecutionMode() {
        String execution = ConfigManager.get("execution");

        if (execution == null || execution.isBlank()) {
            return ExecutionMode.LOCAL;
        }

        return ExecutionMode.valueOf(execution.toUpperCase());
    }

    public static boolean isLocal() {
        return getExecutionMode() == ExecutionMode.LOCAL;
    }

    public static boolean isBrowserStack() {
        return getExecutionMode() == ExecutionMode.BROWSERSTACK;
    }

    public static boolean isSauce() {
        return getExecutionMode() == ExecutionMode.SAUCE;
    }
}
