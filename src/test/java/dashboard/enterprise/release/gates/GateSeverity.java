package dashboard.enterprise.release.gates;

/**
 * Defines the release-governance impact of a quality gate.
 */
public enum GateSeverity {
    BLOCKER,
    HIGH,
    ADVISORY;

    public boolean blocksRelease() {
        return this == BLOCKER;
    }
}
