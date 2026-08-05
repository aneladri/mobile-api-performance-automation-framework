package dashboard.enterprise.agent.integration.work.configuration;

import java.nio.file.Path;
import java.util.Map;

public record WorkManagementConfiguration(
        IntegrationMode mode,
        Path repositoryRoot,
        Map<String, String> environment
) {
    public WorkManagementConfiguration {
        mode = mode == null ? IntegrationMode.REPLAY : mode;
        if (repositoryRoot == null) throw new IllegalArgumentException("Repository root is required.");
        environment = environment == null ? Map.of() : Map.copyOf(environment);
    }
}
