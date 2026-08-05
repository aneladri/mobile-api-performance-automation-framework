package dashboard.enterprise.agent.integration.source.model;

import java.util.List;

public record PullRequest(
        String schemaVersion,
        String provider,
        String id,
        String title,
        String description,
        String sourceBranch,
        String targetBranch,
        String status,
        boolean mergeable,
        List<String> reviewers,
        List<String> changedFiles
) {
    public PullRequest {
        schemaVersion = safe(schemaVersion);
        provider = safe(provider);
        id = safe(id);
        title = safe(title);
        description = safe(description);
        sourceBranch = safe(sourceBranch);
        targetBranch = safe(targetBranch);
        status = safe(status);
        reviewers = reviewers == null ? List.of() : List.copyOf(reviewers);
        changedFiles = changedFiles == null ? List.of() : List.copyOf(changedFiles);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
