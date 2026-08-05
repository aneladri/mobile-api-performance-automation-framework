package performance.enterprise;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EnterprisePerformanceMetrics {

    public String executionId;
    public String scenario;
    public String environment;
    public int requests;
    public int passed;
    public int failed;
    public double availabilityPercent;
    public double errorRatePercent;
    public double throughputPerSecond;
    public double averageMs;
    public double medianMs;
    public double p90Ms;
    public double p95Ms;
    public double p99Ms;
    public double minimumMs;
    public double maximumMs;
    public long durationSeconds;
    public long bytesReceived;
    public long bytesSent;
    public int virtualUsers;
    public int jmeterThreads;
    public String qualityGate;
    public String bottleneck;
    public String recommendation;
    public int confidencePercent;
    public final Map<String, EngineMetrics> engines = new LinkedHashMap<>();

    public static final class EngineMetrics {
        public String engine;
        public int requests;
        public int passed;
        public int failed;
        public double throughputPerSecond;
        public double averageMs;
        public double medianMs;
        public double p90Ms;
        public double p95Ms;
        public double p99Ms;
        public double minimumMs;
        public double maximumMs;
        public double errorRatePercent;
        public long durationSeconds;
        public long bytesReceived;
        public long bytesSent;
        public int concurrency;
    }
}
