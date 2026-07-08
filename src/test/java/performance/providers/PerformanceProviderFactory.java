package performance.providers;

public final class PerformanceProviderFactory {

    private PerformanceProviderFactory() {
    }

    public static PerformanceProvider getProvider() {

        return new JMeterPerformanceProvider();
    }
}
