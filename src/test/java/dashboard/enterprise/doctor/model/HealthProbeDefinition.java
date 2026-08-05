package dashboard.enterprise.doctor.model;

import java.util.List;

/**
 * Versioned metadata describing a MAPAF Doctor health probe.
 */
public record HealthProbeDefinition(
        String id,
        String name,
        String version,
        HealthSeverity severity,
        boolean enabled,
        boolean optional,
        String description,
        List<String> dependencies
) {

    public HealthProbeDefinition {
        require(id, "Probe id");
        require(name, "Probe name");
        require(version, "Probe version");

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Probe severity is required."
            );
        }

        description = description == null ? "" : description;

        dependencies = dependencies == null
                ? List.of()
                : List.copyOf(dependencies);
    }

    private static void require(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
