package dashboard.enterprise.agent.integration.source.model;

import java.util.List;

public record RepositoryChange(
        String schemaVersion,
        String provider,
        String repository,
        String branch,
        String commitId,
        String author,
        int filesChanged,
        int linesAdded,
        int linesRemoved,
        List<String> changedFiles,
        String generatedAt
) {
    public RepositoryChange {
        schemaVersion = safe(schemaVersion);
        provider = safe(provider);
        repository = safe(repository);
        branch = safe(branch);
        commitId = safe(commitId);
        author = safe(author);
        changedFiles = changedFiles == null ? List.of() : List.copyOf(changedFiles);
        generatedAt = safe(generatedAt);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
