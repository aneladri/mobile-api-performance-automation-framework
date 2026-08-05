package performance.providers;

import performance.jmeter.JMeterRunner;
import performance.models.PerformanceResult;

public class JMeterPerformanceProvider
        implements PerformanceProvider {

    private final JMeterRunner runner =
            new JMeterRunner();

    @Override
    public PerformanceResult execute(
            String testPlan
    ) {
        runner.run(
                testPlan,
                "performance/jmeter/results/result.jtl"
        );

        throw new UnsupportedOperationException(
                "JMeter result parsing will be implemented in JM-3."
        );
    }
}
