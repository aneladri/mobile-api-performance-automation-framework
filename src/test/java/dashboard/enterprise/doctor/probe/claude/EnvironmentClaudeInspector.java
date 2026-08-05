package dashboard.enterprise.doctor.probe.claude;

import dashboard.enterprise.doctor.probe.DoctorContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class EnvironmentClaudeInspector
        implements ClaudeInspector {

    @Override
    public ClaudeInspectionResult inspect(
            DoctorContext context
    ) {
        long started = System.nanoTime();

        Map<String, String> environment =
                context.environmentVariables();

        String enabledValue = firstNonBlank(
                environment.get("CLAUDE_ENABLED"),
                System.getProperty("mapaf.claude.enabled"),
                "false"
        );

        boolean providerEnabled =
                Boolean.parseBoolean(enabledValue);

        String apiKey = firstNonBlank(
                environment.get("ANTHROPIC_API_KEY"),
                environment.get("CLAUDE_API_KEY")
        );

        String endpoint = firstNonBlank(
                environment.get("ANTHROPIC_BASE_URL"),
                environment.get("CLAUDE_BASE_URL"),
                System.getProperty("mapaf.claude.endpoint"),
                "https://api.anthropic.com"
        );

        String provider = firstNonBlank(
                environment.get("MAPAF_AI_PROVIDER"),
                "Claude"
        );

        boolean connectivityEnabled =
                Boolean.parseBoolean(
                        System.getProperty(
                                "mapaf.claude.connectivity.enabled",
                                "false"
                        )
                );

        int governedAssets = countGovernedAssets(
                context.repositoryRoot()
        );

        boolean replayAvailable = governedAssets > 0;

        List<String> evidence = new ArrayList<>();

        if (replayAvailable) {
            evidence.add("generated/ai/automation");
        }

        String diagnosis;

        if (providerEnabled
                && !apiKey.isBlank()
                && !endpoint.isBlank()) {
            diagnosis =
                    "Claude live-provider configuration is available.";
        } else if (replayAvailable) {
            diagnosis =
                    "Claude live provider is disabled or incomplete; "
                            + "governed AI-generation replay is available.";
        } else {
            diagnosis =
                    "Claude live provider is not configured and no "
                            + "governed replay assets are available.";
        }

        Map<String, Object> metadata =
                new LinkedHashMap<>();

        metadata.put("connectivityCheckEnabled",
                connectivityEnabled);
        metadata.put("governedAssets", governedAssets);
        metadata.put("providerEnabled", providerEnabled);

        return new ClaudeInspectionResult(
                providerEnabled,
                !apiKey.isBlank(),
                !endpoint.isBlank(),
                connectivityEnabled,
                !connectivityEnabled,
                replayAvailable,
                governedAssets,
                provider,
                endpoint,
                diagnosis,
                elapsedMillis(started),
                evidence,
                metadata
        );
    }

    private int countGovernedAssets(Path root) {
        Path generated = root.resolve(
                "generated/ai/automation"
        );

        if (!Files.isDirectory(generated)) {
            return 0;
        }

        try (Stream<Path> stream = Files.walk(generated)) {
            return (int) stream
                    .filter(Files::isRegularFile)
                    .count();

        } catch (Exception exception) {
            return 0;
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
