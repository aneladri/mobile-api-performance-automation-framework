package dashboard.enterprise.release.gates;

/**
 * Standard status contract for MAPAF release quality gates.
 */
public enum GateStatus {
    PASS,
    WARN,
    FAIL,
    NOT_RUN;

    public boolean passed() {
        return this == PASS;
    }

    public boolean failed() {
        return this == FAIL || this == NOT_RUN;
    }
}
