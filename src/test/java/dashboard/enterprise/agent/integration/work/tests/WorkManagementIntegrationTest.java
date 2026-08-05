package dashboard.enterprise.agent.integration.work.tests;

import dashboard.enterprise.agent.integration.work.model.*;
import dashboard.enterprise.agent.integration.work.skill.DeterministicTestDesignSkill;
import dashboard.enterprise.agent.integration.work.tool.DraftTestTaskCreateTool;
import dashboard.enterprise.agent.integration.work.tool.ReplayWorkItemReadTool;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.ToolRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Map;

public final class WorkManagementIntegrationTest {
    @Test
    public void shouldReadJiraReplayAndGenerateTests() throws Exception {
        var context = new AgentContext(Path.of("."), "CORR-JIRA", "demo-user", Map.of());
        var result = new ReplayWorkItemReadTool("jira").execute(context, new ToolRequest("read", Map.of("id", "RS-101")));
        WorkItem item = (WorkItem) result.data().get("workItem");
        TestDesign design = new DeterministicTestDesignSkill().generate(item);
        Assert.assertTrue(result.successful());
        Assert.assertEquals(item.provider(), "JIRA");
        Assert.assertEquals(design.scenarios().size(), 6);
    }

    @Test
    public void shouldReadAzureReplay() throws Exception {
        var context = new AgentContext(Path.of("."), "CORR-ADO", "demo-user", Map.of());
        var result = new ReplayWorkItemReadTool("azure").execute(context, new ToolRequest("read", Map.of("id", "501")));
        WorkItem item = (WorkItem) result.data().get("workItem");
        Assert.assertEquals(item.provider(), "AZURE_BOARDS");
    }

    @Test
    public void shouldCreateApprovedDraftTasksInReplayMode() {
        var context = new AgentContext(Path.of("."), "CORR-WRITE", "approver", Map.of());
        var result = new DraftTestTaskCreateTool("jira").execute(context,
                new ToolRequest("create", Map.of("approvedBy", "qe-lead")));
        Assert.assertTrue(result.successful());
        Assert.assertEquals(((java.util.List<?>) result.data().get("draftIds")).size(), 2);
    }
}
