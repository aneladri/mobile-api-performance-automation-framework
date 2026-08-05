package dashboard.enterprise.agent.integration.source.tool;

import dashboard.enterprise.agent.integration.source.model.ImpactAnalysis;
import dashboard.enterprise.agent.integration.source.model.RepositoryChange;
import dashboard.enterprise.agent.integration.source.skill.DeterministicImpactAnalysisSkill;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.util.List;
import java.util.Map;

public final class ImpactAnalysisTool implements AgentTool {
    private final DeterministicImpactAnalysisSkill skill = new DeterministicImpactAnalysisSkill();

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "impact-analysis-generate",
                "Impact Analysis Generate",
                "Generates deterministic change impact and recommended test suites.",
                PermissionLevel.AUTONOMOUS_WRITE
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        Object value = request.parameters().get("repositoryChange");
        if (!(value instanceof RepositoryChange change)) {
            return new ToolResult(false, "repositoryChange parameter is required.", List.of(), Map.of());
        }
        ImpactAnalysis analysis = skill.analyze(change);
        return new ToolResult(
                true,
                "Impact analysis generated with " + analysis.risk() + " risk.",
                List.of("mapaf.impact-analysis/v1"),
                Map.of("impactAnalysis", analysis)
        );
    }
}
