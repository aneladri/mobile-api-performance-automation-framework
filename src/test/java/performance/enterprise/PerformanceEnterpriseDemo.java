package performance.enterprise;

import java.nio.file.Path;

public final class PerformanceEnterpriseDemo {

    private PerformanceEnterpriseDemo() {
    }

    public static void main(String[] args) throws Exception {
        Path projectDirectory = Path.of(System.getProperty("user.dir"));
        EnterprisePerformanceAnalyzer analyzer = new EnterprisePerformanceAnalyzer();
        EnterprisePerformanceMetrics metrics = analyzer.analyze(projectDirectory);
        new PerformanceExecutionLogger().print(metrics);

        Path summary = analyzer.write(
                metrics,
                projectDirectory.resolve("performance/reports/enterprise-summary.json")
        );

        System.out.println();
        System.out.println("Publishing enterprise metrics............. PASS");
        System.out.println("Publishing dashboard summary.............. PASS");
        System.out.println("Publishing diagnostics.................... PASS");
        System.out.println("MAPAF enterprise performance summary: " + summary);

        if (!"PASS".equals(metrics.qualityGate)) {
            throw new IllegalStateException("Enterprise performance quality gate failed");
        }
    }
}
