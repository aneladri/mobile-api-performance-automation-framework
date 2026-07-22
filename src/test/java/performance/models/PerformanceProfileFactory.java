package performance.models;

public final class PerformanceProfileFactory {

    private PerformanceProfileFactory() {
    }

    public static PerformanceProfile smoke() {
        return new PerformanceProfile(
                PerformanceProfileType.SMOKE,
                1,
                1,
                30
        );
    }

    public static PerformanceProfile load() {
        return new PerformanceProfile(
                PerformanceProfileType.LOAD,
                50,
                60,
                600
        );
    }

    public static PerformanceProfile stress() {
        return new PerformanceProfile(
                PerformanceProfileType.STRESS,
                250,
                120,
                1800
        );
    }

    public static PerformanceProfile spike() {
        return new PerformanceProfile(
                PerformanceProfileType.SPIKE,
                500,
                30,
                300
        );
    }

    public static PerformanceProfile soak() {
        return new PerformanceProfile(
                PerformanceProfileType.SOAK,
                50,
                300,
                14400
        );
    }
}
