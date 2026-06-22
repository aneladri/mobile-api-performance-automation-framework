package ai.tests;

import core.ai.AgentTrendAnalyzer;
import core.ai.ExecutionRecord;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class AgentTrendAnalyzerTest {

    @Test
    public void verifyAverageConfidence() {

        List<ExecutionRecord> records = List.of(
                new ExecutionRecord("failure-analysis", "SSL Configuration Failure", 95),
                new ExecutionRecord("failure-analysis", "BrowserStack Account Limit Failure", 99)
        );

        AgentTrendAnalyzer analyzer = new AgentTrendAnalyzer();

        Assert.assertEquals(
                analyzer.averageConfidence(records),
                97
        );
    }

    @Test
    public void verifyClassificationCounts() {

        List<ExecutionRecord> records = List.of(
                new ExecutionRecord("failure-analysis", "SSL Configuration Failure", 95),
                new ExecutionRecord("failure-analysis", "SSL Configuration Failure", 90)
        );

        AgentTrendAnalyzer analyzer = new AgentTrendAnalyzer();

        Map<String, Long> counts =
                analyzer.classificationCounts(records);

        Assert.assertEquals(
                counts.get("SSL Configuration Failure").longValue(),
                2
        );
    }
}
