package core.enterprise.evidence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EvidenceRegistry {
    private final List<EvidenceArtifact> artifacts = new ArrayList<>();
    public void register(EvidenceArtifact artifact) {
        if (artifact == null) throw new IllegalArgumentException("Evidence artifact must not be null");
        artifacts.add(artifact);
    }
    public List<EvidenceArtifact> getArtifacts() {
        return Collections.unmodifiableList(new ArrayList<>(artifacts));
    }
    public long count(EvidenceType type) { return artifacts.stream().filter(a -> a.type() == type).count(); }
    public int size() { return artifacts.size(); }
}
