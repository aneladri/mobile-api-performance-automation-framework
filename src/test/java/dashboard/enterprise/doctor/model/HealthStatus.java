package dashboard.enterprise.doctor.model;

/**
 * Standard operational status for MAPAF Doctor probes.
 */
public enum HealthStatus {
    HEALTHY,
    DEGRADED,
    UNHEALTHY,
    NOT_CONFIGURED;

    public boolean operational() {
        return this == HEALTHY || this == DEGRADED;
    }

    public boolean unhealthy() {
        return this == UNHEALTHY;
    }
}
