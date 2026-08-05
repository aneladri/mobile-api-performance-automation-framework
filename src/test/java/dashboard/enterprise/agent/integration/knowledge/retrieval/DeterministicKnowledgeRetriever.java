package dashboard.enterprise.agent.integration.knowledge.retrieval;

import dashboard.enterprise.agent.integration.knowledge.model.*;
import dashboard.enterprise.agent.integration.knowledge.registry.KnowledgeRegistry;

import java.util.*;
import java.util.stream.Collectors;

public final class DeterministicKnowledgeRetriever implements KnowledgeRetriever {
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "and", "or", "to", "for", "of", "in", "on", "is", "are", "what", "should", "we"
    );

    private final KnowledgeRegistry registry;

    public DeterministicKnowledgeRetriever(KnowledgeRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "Knowledge registry is required.");
    }

    @Override
    public KnowledgeResult search(KnowledgeQuery query) {
        Set<String> terms = tokenize(query.text());
        List<KnowledgeMatch> matches = registry.documents().stream()
                .filter(document -> query.sources().isEmpty() || query.sources().contains(document.source()))
                .filter(document -> query.requiredTags().isEmpty() || document.tags().containsAll(query.requiredTags()))
                .map(document -> match(document, terms))
                .filter(match -> match.relevanceScore() > 0)
                .sorted(Comparator.comparingInt(KnowledgeMatch::relevanceScore).reversed()
                        .thenComparing(KnowledgeMatch::documentId))
                .limit(query.maxResults())
                .toList();

        List<String> evidence = matches.stream()
                .map(match -> match.sourceUri().isBlank() ? match.documentId() : match.sourceUri())
                .toList();

        String summary = matches.isEmpty()
                ? "No relevant project knowledge was found."
                : "Found " + matches.size() + " relevant knowledge document(s). Top result: " + matches.get(0).title();

        return new KnowledgeResult(
                "mapaf.knowledge.result/v1",
                query.queryId(),
                summary,
                registry.size(),
                matches,
                evidence
        );
    }

    private KnowledgeMatch match(KnowledgeDocument document, Set<String> terms) {
        String searchable = (document.title() + " " + document.content() + " " + String.join(" ", document.tags()))
                .toLowerCase(Locale.ROOT);
        List<String> matched = terms.stream().filter(searchable::contains).sorted().toList();
        int score = Math.min(100, matched.size() * 18 + titleBonus(document.title(), matched) + tagBonus(document.tags(), matched));
        return new KnowledgeMatch(
                document.documentId(),
                document.title(),
                document.source(),
                score,
                excerpt(document.content(), matched),
                document.sourceUri(),
                matched
        );
    }

    private static int titleBonus(String title, List<String> matched) {
        String normalized = title.toLowerCase(Locale.ROOT);
        return matched.stream().anyMatch(normalized::contains) ? 15 : 0;
    }

    private static int tagBonus(List<String> tags, List<String> matched) {
        String normalized = String.join(" ", tags).toLowerCase(Locale.ROOT);
        return matched.stream().anyMatch(normalized::contains) ? 10 : 0;
    }

    private static String excerpt(String content, List<String> matched) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        int start = 0;
        for (String term : matched) {
            int index = normalized.toLowerCase(Locale.ROOT).indexOf(term);
            if (index >= 0) {
                start = Math.max(0, index - 60);
                break;
            }
        }
        int end = Math.min(normalized.length(), start + 220);
        return normalized.substring(start, end);
    }

    private static Set<String> tokenize(String text) {
        if (text == null) {
            return Set.of();
        }
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9_-]+"))
                .map(String::trim)
                .filter(token -> token.length() > 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
