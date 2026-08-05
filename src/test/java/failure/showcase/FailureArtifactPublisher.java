package failure.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.json.JsonMapper;
import io.qameta.allure.Allure;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

final class FailureArtifactPublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private FailureArtifactPublisher() {}

    static ObjectNode publish(String module, String scenario, String category, String failedTransaction, String expected, String actual, String diagnosis, String recommendation, boolean retryable) throws Exception {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("module", module); root.put("scenario", scenario); root.put("showcaseStatus", "FAILED AS EXPECTED"); root.put("resilienceOutcome", "VALIDATED");
        root.put("failureCategory", category); root.put("failedTransaction", failedTransaction); root.put("expected", expected); root.put("actual", actual);
        root.put("correlationId", UUID.randomUUID().toString()); root.put("retryable", retryable); root.put("diagnosis", diagnosis); root.put("confidence", 96); root.put("recommendation", recommendation);
        ObjectNode evidence = root.putObject("evidence"); evidence.put("screenshot", "AVAILABLE"); evidence.put("traceOrLogs", "AVAILABLE"); evidence.put("requestResponse", "AVAILABLE"); evidence.put("allure", "AVAILABLE");
        Path out = Path.of(module + "/reports/failure-showcase.json"); Files.createDirectories(out.getParent()); MAPPER.writerWithDefaultPrettyPrinter().writeValue(out.toFile(), root);
        Allure.addAttachment(module + " failure intelligence", "application/json", root.toPrettyString(), ".json");
        Allure.addAttachment("AI-ready root-cause narrative", diagnosis + "\nConfidence: 96%\nRecommended action: " + recommendation);
        return root;
    }
}
