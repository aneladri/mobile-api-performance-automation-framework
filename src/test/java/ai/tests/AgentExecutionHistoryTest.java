package ai.tests;

import core.ai.AgentExecutionHistory;
import core.ai.ExecutionRecord;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AgentExecutionHistoryTest {

    @BeforeMethod
    public void resetHistory() {
        AgentExecutionHistory.reset();
    }

    @Test
    public void verifyExecutionHistoryIsRecorded() {

        ExecutionRecord record =
                new ExecutionRecord(
                        "failure-analysis",
                        "SSL Configuration Failure",
                        95
                );

        AgentExecutionHistory.record(record);

        Assert.assertEquals(
                AgentExecutionHistory.getRecords().size(),
                1
        );

        Assert.assertEquals(
                AgentExecutionHistory.getRecords().get(0).getAgentName(),
                "failure-analysis"
        );
    }
}
