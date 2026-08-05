package core.enterprise.evidence;

import java.time.Instant;

public record EvidenceArtifact(String name, EvidenceType type, String path,
                               String contentType, Instant capturedAt) {
    public EvidenceArtifact {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Evidence name must not be blank");
        if (type == null) throw new IllegalArgumentException("Evidence type must not be null");
        capturedAt = capturedAt == null ? Instant.now() : capturedAt;
    }
}
