package dashboard.enterprise.agent.integration.source.skill;

import dashboard.enterprise.agent.integration.source.model.ImpactAnalysis;
import dashboard.enterprise.agent.integration.source.model.RepositoryChange;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class DeterministicImpactAnalysisSkill {

    public ImpactAnalysis analyze(RepositoryChange change) {
        Set<String> modules = new LinkedHashSet<>();
        Set<String> suites = new LinkedHashSet<>();
        List<String> reasons = new ArrayList<>();
        int score = Math.min(60, change.filesChanged() * 4);

        for (String file : change.changedFiles()) {
            String f = file.toLowerCase();
            if (f.contains("mobile") || f.contains("android") || f.contains("ios")) {
                modules.add("RoomScan Mobile");
                suites.add("mobile-regression");
                score += 12;
                reasons.add("Mobile application code changed: " + file);
            }
            if (f.contains("api") || f.contains("controller") || f.contains("service")) {
                modules.add("RoomScan Backend API");
                suites.add("api-regression");
                suites.add("contract-tests");
                score += 10;
                reasons.add("API or service code changed: " + file);
            }
            if (f.contains("performance") || f.contains("upload") || f.contains("worker")) {
                modules.add("RoomScan Performance");
                suites.add("performance-smoke");
                score += 8;
                reasons.add("Performance-sensitive path changed: " + file);
            }
            if (f.endsWith(".gradle") || f.contains("pipeline") || f.contains("workflow")) {
                modules.add("Build and Delivery");
                suites.add("framework-smoke");
                score += 8;
                reasons.add("Build or pipeline definition changed: " + file);
            }
            if (f.contains("security") || f.contains("auth")) {
                modules.add("Security");
                suites.add("security-regression");
                score += 15;
                reasons.add("Security-sensitive code changed: " + file);
            }
        }

        if (suites.isEmpty()) {
            suites.add("targeted-regression");
            modules.add("General Platform");
            reasons.add("General source changes require targeted regression.");
        }

        score = Math.max(0, Math.min(100, score));
        String risk = score >= 75 ? "HIGH" : score >= 40 ? "MEDIUM" : "LOW";
        int minutes = Math.max(10, suites.size() * 8);
        return new ImpactAnalysis(
                "mapaf.impact-analysis/v1",
                risk,
                score,
                List.copyOf(modules),
                List.copyOf(suites),
                reasons,
                minutes,
                score >= 75
        );
    }
}
