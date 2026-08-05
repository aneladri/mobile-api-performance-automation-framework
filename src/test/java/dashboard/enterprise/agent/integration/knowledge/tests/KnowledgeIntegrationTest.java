package dashboard.enterprise.agent.integration.knowledge.tests;

import dashboard.enterprise.agent.audit.InMemoryAgentAuditStore;
import dashboard.enterprise.agent.engine.DefaultAgentEngine;
import dashboard.enterprise.agent.engine.InMemoryAgentRegistry;
import dashboard.enterprise.agent.integration.knowledge.evidence.KnowledgeEvidencePublisher;
import dashboard.enterprise.agent.integration.knowledge.model.*;
import dashboard.enterprise.agent.integration.knowledge.registry.InMemoryKnowledgeRegistry;
import dashboard.enterprise.agent.integration.knowledge.retrieval.DeterministicKnowledgeRetriever;
import dashboard.enterprise.agent.integration.knowledge.retrieval.KnowledgeFixtureLoader;
import dashboard.enterprise.agent.integration.knowledge.tool.*;
import dashboard.enterprise.agent.model.*;
import dashboard.enterprise.agent.policy.AgentPermissionEvaluator;
import dashboard.enterprise.agent.skill.InMemorySkillRegistry;
import dashboard.enterprise.agent.skill.SkillDefinition;
import dashboard.enterprise.agent.tool.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class KnowledgeIntegrationTest {

    @Test
    public void shouldSearchLocalAndReplayKnowledge() throws Exception {
        Path root = Path.of(".").toAbsolutePath().normalize();
        var registry = registryFromFixtures(root);
        var retriever = new DeterministicKnowledgeRetriever(registry);

        ToolResult result = new KnowledgeSearchTool(retriever).execute(
                new AgentContext(root, "KN-1", "demo-user", Map.of()),
                new ToolRequest("search", Map.of(
                        "query", new KnowledgeQuery(
                                "mapaf.knowledge.query/v1",
                                "Q-1",
                                "RoomScan image upload retry object storage performance",
                                List.of(),
                                List.of(),
                                5
                        )
                ))
        );

        Assert.assertTrue(result.successful());
        KnowledgeResult knowledge = (KnowledgeResult) result.data().get("knowledgeResult");
        Assert.assertFalse(knowledge.matches().isEmpty());
        Assert.assertTrue(
                knowledge.matches().stream()
                        .anyMatch(match ->
                                "roomscan-upload-architecture"
                                        .equals(match.documentId())
                        ),
                "Expected RoomScan upload architecture evidence."
        );
        Assert.assertTrue(
                knowledge.evidence().stream()
                        .anyMatch(value -> value.contains("roomscan-upload"))
        );
    }

    @Test
    public void shouldReadKnowledgeDocument() throws Exception {
        Path root = Path.of(".").toAbsolutePath().normalize();
        var registry = registryFromFixtures(root);
        ToolResult result = new KnowledgeDocumentReadTool(registry).execute(
                new AgentContext(root, "KN-2", "demo-user", Map.of()),
                new ToolRequest("read", Map.of("documentId", "roomscan-api-guidelines"))
        );

        Assert.assertTrue(result.successful());
        KnowledgeDocument document = (KnowledgeDocument) result.data().get("knowledgeDocument");
        Assert.assertTrue(document.content().contains("multipart"));
    }

    @Test
    public void shouldPublishEvidence() throws Exception {
        Path root = Files.createTempDirectory("mapaf-knowledge-test");
        var registry = new InMemoryKnowledgeRegistry();
        registry.register(new KnowledgeDocument(
                "mapaf.knowledge.document/v1",
                "doc-1",
                KnowledgeSource.LOCAL_REPOSITORY,
                "Upload Architecture",
                "Image upload uses retry and object storage.",
                "docs/upload.md",
                List.of("upload"),
                Map.of()
        ));
        KnowledgeResult result = new DeterministicKnowledgeRetriever(registry).search(
                new KnowledgeQuery("mapaf.knowledge.query/v1", "Q-2", "upload retry", List.of(), List.of(), 5)
        );
        Path evidence = KnowledgeEvidencePublisher.publish(root, "KN-3", "upload retry", result);
        Assert.assertTrue(Files.isRegularFile(evidence));
        Assert.assertTrue(Files.readString(evidence).contains("mapaf.knowledge.evidence/v1"));
    }

    @Test(expectedExceptions = SecurityException.class)
    public void shouldRequireApprovalBeforeRefreshingIndex() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();

        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1",
                "knowledge-agent",
                "Knowledge Agent",
                "Searches and refreshes governed project knowledge.",
                List.of("knowledge-retrieval"),
                List.of("knowledge-index-refresh"),
                ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES
        ));
        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1",
                "knowledge-retrieval",
                "Knowledge Retrieval",
                "Retrieve project knowledge and refresh approved indexes.",
                List.of("knowledge-index-refresh"),
                List.of("Require human approval before index refresh")
        ));
        tools.register(new KnowledgeIndexRefreshTool());

        new DefaultAgentEngine(
                agents,
                skills,
                tools,
                new AgentPermissionEvaluator(),
                new InMemoryAgentAuditStore()
        ).execute(
                "knowledge-agent",
                "knowledge-retrieval",
                "knowledge-index-refresh",
                new AgentContext(Path.of("."), "KN-4", "demo-user", Map.of()),
                new ToolRequest("refresh", Map.of("source", "LOCAL_REPOSITORY")),
                false
        );
    }

    @Test(expectedExceptions = SecurityException.class)
    public void shouldProhibitKnowledgeSourceDeletion() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();

        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1",
                "knowledge-admin",
                "Knowledge Administrator",
                "Administers knowledge sources.",
                List.of("knowledge-admin"),
                List.of("knowledge-source-delete"),
                ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES
        ));
        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1",
                "knowledge-admin",
                "Knowledge Administration",
                "Govern knowledge sources.",
                List.of("knowledge-source-delete"),
                List.of()
        ));
        tools.register(new KnowledgeSourceDeleteTool());

        new DefaultAgentEngine(
                agents,
                skills,
                tools,
                new AgentPermissionEvaluator(),
                new InMemoryAgentAuditStore()
        ).execute(
                "knowledge-admin",
                "knowledge-admin",
                "knowledge-source-delete",
                new AgentContext(Path.of("."), "KN-5", "demo-user", Map.of()),
                new ToolRequest("delete", Map.of("source", "REPLAY_SHAREPOINT")),
                true
        );
    }

    private static InMemoryKnowledgeRegistry registryFromFixtures(Path root) throws Exception {
        var registry = new InMemoryKnowledgeRegistry();
        for (String relative : List.of(
                "src/test/resources/knowledge/local/documents.json",
                "src/test/resources/knowledge/confluence/documents.json",
                "src/test/resources/knowledge/sharepoint/documents.json",
                "src/test/resources/knowledge/google-drive/documents.json"
        )) {
            for (KnowledgeDocument document : KnowledgeFixtureLoader.load(root.resolve(relative))) {
                registry.register(document);
            }
        }
        return registry;
    }
}
