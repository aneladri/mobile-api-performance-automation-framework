package dashboard.enterprise.agent.orchestration.engine;

import dashboard.enterprise.agent.engine.DefaultAgentEngine;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.model.AgentExecutionResult;
import dashboard.enterprise.agent.orchestration.model.*;
import dashboard.enterprise.agent.orchestration.policy.WorkflowPolicyEvaluator;
import dashboard.enterprise.agent.tool.ToolRequest;

import java.util.*;

public final class WorkflowOrchestrator {
    private final WorkflowRegistry workflows;
    private final DefaultAgentEngine agentEngine;
    private final WorkflowPolicyEvaluator policy;

    public WorkflowOrchestrator(WorkflowRegistry workflows, DefaultAgentEngine agentEngine, WorkflowPolicyEvaluator policy) {
        this.workflows = workflows;
        this.agentEngine = agentEngine;
        this.policy = policy;
    }

    public WorkflowExecution execute(
            String workflowId,
            String executionId,
            AgentContext baseContext,
            Map<String, Boolean> approvals
    ) throws Exception {
        MultiAgentWorkflow workflow = workflows.require(workflowId);
        Map<String, Object> shared = new LinkedHashMap<>(baseContext.attributes());
        List<WorkflowStepResult> stepResults = new ArrayList<>();
        List<AgentHandoff> handoffs = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        boolean warned = false;
        String previousAgent = "workflow-orchestrator";

        for (WorkflowStep step : workflow.steps()) {
            boolean approved = approvals != null && Boolean.TRUE.equals(approvals.get(step.stepId()));
            try {
                policy.verify(step, approved);
            } catch (SecurityException ex) {
                return new WorkflowExecution(
                        "mapaf.agent.workflow-execution/v1",
                        executionId,
                        workflowId,
                        WorkflowStatus.WAITING_FOR_APPROVAL,
                        ex.getMessage(),
                        stepResults,
                        handoffs,
                        evidence,
                        shared
                );
            }

            Map<String, Object> input = new LinkedHashMap<>(shared);
            input.putAll(step.input());
            AgentContext stepContext = new AgentContext(
                    baseContext.repositoryRoot(),
                    baseContext.correlationId(),
                    baseContext.actor(),
                    input
            );

            AgentExecutionResult result;
            int attempts = 1;
            try {
                result = agentEngine.execute(
                        step.agentId(), step.skillId(), step.toolId(),
                        stepContext, new ToolRequest("execute", input), approved
                );
                if (!"SUCCESS".equals(result.status()) && step.failurePolicy() == WorkflowFailurePolicy.RETRY_ONCE) {
                    attempts++;
                    result = agentEngine.execute(
                            step.agentId(), step.skillId(), step.toolId(),
                            stepContext, new ToolRequest("execute", input), approved
                    );
                }
            } catch (Exception ex) {
                if (step.optional() || step.failurePolicy() == WorkflowFailurePolicy.SKIP_OPTIONAL_STEP) {
                    warned = true;
                    stepResults.add(new WorkflowStepResult(step.stepId(), step.agentId(), "SKIPPED", ex.getMessage(), attempts, List.of(), Map.of()));
                    continue;
                }
                if (step.failurePolicy() == WorkflowFailurePolicy.CONTINUE_WITH_WARNING) {
                    warned = true;
                    stepResults.add(new WorkflowStepResult(step.stepId(), step.agentId(), "WARNING", ex.getMessage(), attempts, List.of(), Map.of()));
                    continue;
                }
                stepResults.add(new WorkflowStepResult(step.stepId(), step.agentId(), "FAILED", ex.getMessage(), attempts, List.of(), Map.of()));
                return new WorkflowExecution(
                        "mapaf.agent.workflow-execution/v1", executionId, workflowId,
                        WorkflowStatus.FAILED, "Workflow failed at step " + step.stepId(),
                        stepResults, handoffs, evidence, shared
                );
            }

            shared.putAll(result.output());
            evidence.addAll(result.evidence());
            stepResults.add(new WorkflowStepResult(
                    step.stepId(), step.agentId(), result.status(), result.summary(),
                    attempts, result.evidence(), result.output()
            ));
            handoffs.add(new AgentHandoff(
                    "mapaf.agent.handoff/v1", previousAgent, step.agentId(), step.stepId(), result.output()
            ));
            previousAgent = step.agentId();
        }

        return new WorkflowExecution(
                "mapaf.agent.workflow-execution/v1",
                executionId,
                workflowId,
                warned ? WorkflowStatus.PASSED_WITH_WARNINGS : WorkflowStatus.PASSED,
                warned ? "Workflow completed with warnings." : "Workflow completed successfully.",
                stepResults,
                handoffs,
                evidence,
                shared
        );
    }
}
