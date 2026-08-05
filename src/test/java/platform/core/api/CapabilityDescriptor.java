package platform.core.api;

import java.util.Objects;

/** Immutable identity and version metadata for a registered capability. */
public record CapabilityDescriptor(
        String id,
        String displayName,
        String version,
        CapabilityType type
) {
    public CapabilityDescriptor {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        version = requireText(version, "version");
        type = Objects.requireNonNull(type, "type");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
