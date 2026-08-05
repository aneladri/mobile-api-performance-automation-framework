package dashboard.enterprise.agent.integration.work.tool;

import dashboard.enterprise.agent.integration.work.model.TestDesign;
import dashboard.enterprise.agent.integration.work.model.WorkItem;
import dashboard.enterprise.agent.integration.work.skill.DeterministicTestDesignSkill;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.util.List;
import java.util.Map;

public final class TestDesignGenerateTool implements AgentTool {
    @Override public ToolDefinition definition() {
        return new ToolDefinition("mapaf.agent.tool/v1", "test-design-generate", "Test Design Generate",
                "Generates governed test scenarios from a normalized work item.", PermissionLevel.AUTONOMOUS_WRITE);
    }
    @Override public ToolResult execute(AgentContext context, ToolRequest request) {
        Object raw = request.parameters().get("workItem");
        if (!(raw instanceof WorkItem item)) throw new IllegalArgumentException("workItem parameter is required.");
        TestDesign design = new DeterministicTestDesignSkill().generate(item);
        return new ToolResult(true, "Generated " + design.scenarios().size() + " test scenarios.",
                List.of("mapaf.test-design/v1"), Map.of("testDesign", design));
    }
}
