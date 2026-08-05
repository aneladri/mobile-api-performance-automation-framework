package dashboard.enterprise.doctor.probe;

import java.nio.file.Path;
import java.util.Map;

/**
 * Immutable runtime context supplied to MAPAF Doctor probes.
 */
public record DoctorContext(
        Path repositoryRoot,
        String platformVersion,
        String environment,
        Map<String, String> environmentVariables,
        Map<String, Object> attributes
) {

    public DoctorContext {
        if (repositoryRoot == null) {
            throw new IllegalArgumentException(
                    "Doctor repository root is required."
            );
        }

        platformVersion = platformVersion == null
                ? "UNKNOWN"
                : platformVersion;

        environment = environment == null
                ? "LOCAL"
                : environment;

        environmentVariables = environmentVariables == null
                ? Map.of()
                : Map.copyOf(environmentVariables);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public String environmentVariable(String name) {
        return environmentVariables.getOrDefault(name, "");
    }

    public Object attribute(String name) {
        return attributes.get(name);
    }
}
