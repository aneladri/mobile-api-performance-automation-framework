package performance.models;

public class PerformanceProfile {

    private final PerformanceProfileType type;
    private final int virtualUsers;
    private final int rampUpSeconds;
    private final int durationSeconds;

    public PerformanceProfile(
            PerformanceProfileType type,
            int virtualUsers,
            int rampUpSeconds,
            int durationSeconds
    ) {
        this.type = type;
        this.virtualUsers = virtualUsers;
        this.rampUpSeconds = rampUpSeconds;
        this.durationSeconds = durationSeconds;
    }

    public PerformanceProfileType getType() {
        return type;
    }

    public int getVirtualUsers() {
        return virtualUsers;
    }

    public int getRampUpSeconds() {
        return rampUpSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}
