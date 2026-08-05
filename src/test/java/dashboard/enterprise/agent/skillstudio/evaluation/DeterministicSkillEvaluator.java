package dashboard.enterprise.agent.skillstudio.evaluation;

import dashboard.enterprise.agent.skillstudio.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DeterministicSkillEvaluator {
    public SkillEvaluationResult evaluate(
            SkillPackage skillPackage,
            List<SkillTestCase> testCases,
            Map<String, Object> actualOutput
    ) {
        List<SkillTestCase> safeCases = testCases == null ? List.of() : List.copyOf(testCases);
        int passed = 0;
        List<String> evidence = new ArrayList<>();
        for (SkillTestCase testCase : safeCases) {
            boolean matches = testCase.expectedOutput().entrySet().stream()
                    .allMatch(entry -> java.util.Objects.equals(actualOutput.get(entry.getKey()), entry.getValue()));
            if (matches) {
                passed++;
                evidence.add(testCase.testId() + ":PASS");
            } else {
                evidence.add(testCase.testId() + ":FAIL");
            }
        }
        int total = safeCases.size();
        int failed = total - passed;
        double score = total == 0 ? 0 : (passed * 100.0) / total;
        return new SkillEvaluationResult(
                "mapaf.skill.evaluation/v1",
                skillPackage.coordinate(),
                total,
                passed,
                failed,
                score,
                evidence
        );
    }
}
