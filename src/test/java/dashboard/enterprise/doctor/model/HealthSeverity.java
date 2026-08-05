package dashboard.enterprise.doctor.model;

/**
 * Describes the operational impact of a health probe.
 */
public enum HealthSeverity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    INFO;

    public boolean critical() {
        return this == CRITICAL;
    }
}
