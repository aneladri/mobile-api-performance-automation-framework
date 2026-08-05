package dashboard.enterprise.doctor.probe.environment;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DiagnosticStatus;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.HealthProbe;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnvironmentHealthProbe implements HealthProbe {

    private static final Duration COMMAND_TIMEOUT =
            Duration.ofSeconds(10);

    private final CommandExecutor commandExecutor;
    private final int doctorPort;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "environment-health",
                    "Environment Health",
                    "1.0",
                    HealthSeverity.CRITICAL,
                    true,
                    false,
                    "Validates MAPAF runtime tools, filesystem access, "
                            + "required directories, and local port readiness.",
                    List.of("repository-foundation")
            );

    public EnvironmentHealthProbe() {
        this(
                new ProcessCommandExecutor(),
                Integer.parseInt(
                        System.getProperty(
                                "mapaf.doctor.port",
                                "8090"
                        )
                )
        );
    }

    public EnvironmentHealthProbe(
            CommandExecutor commandExecutor,
            int doctorPort
    ) {
        if (commandExecutor == null) {
            throw new IllegalArgumentException(
                    "Command executor is required."
            );
        }

        if (doctorPort < 1 || doctorPort > 65535) {
            throw new IllegalArgumentException(
                    "Doctor port must be between 1 and 65535."
            );
        }

        this.commandExecutor = commandExecutor;
        this.doctorPort = doctorPort;
    }

    @Override
    public HealthProbeDefinition definition() {
        return definition;
    }

    @Override
    public HealthProbeResult execute(DoctorContext context) {
        long started = System.nanoTime();

        List<DiagnosticCheck> checks = new ArrayList<>();

        checks.add(commandCheck(
                "java-runtime",
                "Java Runtime",
                List.of("java", "-version"),
                "Java runtime is installed and executable",
                true
        ));

        checks.add(commandCheck(
                "gradle-wrapper",
                "Gradle Wrapper",
                List.of(
                        context.repositoryRoot()
                                .resolve("gradlew")
                                .toString(),
                        "--version"
                ),
                "Gradle wrapper is executable",
                true
        ));

        checks.add(commandCheck(
                "python-runtime",
                "Python Runtime",
                List.of("python3", "--version"),
                "Python 3 is installed and executable",
                true
        ));

        checks.add(commandCheck(
                "node-runtime",
                "Node.js Runtime",
                List.of("node", "--version"),
                "Node.js is installed and executable",
                false
        ));

        checks.add(commandCheck(
                "npm-runtime",
                "npm Runtime",
                List.of("npm", "--version"),
                "npm is installed and executable",
                false
        ));

        checks.add(repositoryWriteCheck(
                context.repositoryRoot()
        ));

        checks.add(requiredDirectoryCheck(
                context.repositoryRoot(),
                "src/test/java",
                "Test source directory"
        ));

        checks.add(requiredDirectoryCheck(
                context.repositoryRoot(),
                "scripts",
                "Scripts directory"
        ));

        checks.add(portCheck(doctorPort));

        int failedCritical = 0;
        int warnings = 0;
        int passed = 0;

        for (DiagnosticCheck check : checks) {
            switch (check.status()) {
                case PASS -> passed++;
                case WARN, SKIPPED -> warnings++;
                case FAIL -> {
                    if (isCriticalCheck(check.id())) {
                        failedCritical++;
                    } else {
                        warnings++;
                    }
                }
            }
        }

        HealthStatus status;

        if (failedCritical > 0) {
            status = HealthStatus.UNHEALTHY;
        } else if (warnings > 0) {
            status = HealthStatus.DEGRADED;
        } else {
            status = HealthStatus.HEALTHY;
        }

        List<String> actions = checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.FAIL
                                || check.status()
                                == DiagnosticStatus.WARN)
                .map(DiagnosticCheck::correctiveAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("checksTotal", checks.size());
        metadata.put("checksPassed", passed);
        metadata.put("warnings", warnings);
        metadata.put("criticalFailures", failedCritical);
        metadata.put("doctorPort", doctorPort);

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                elapsedMillis(started),
                summary(status, passed, checks.size()),
                diagnosis(status, failedCritical, warnings),
                actions,
                checks,
                List.of(
                        "MAPAF_VERSION",
                        "gradlew",
                        "src/test/java",
                        "scripts"
                ),
                metadata
        );
    }

    private DiagnosticCheck commandCheck(
            String id,
            String name,
            List<String> command,
            String expected,
            boolean critical
    ) {
        CommandResult result =
                commandExecutor.execute(
                        command,
                        COMMAND_TIMEOUT
                );

        String actual = result.combinedOutput();

        if (actual.isBlank()) {
            actual = "No command output";
        }

        actual = firstLine(actual);

        if (result.successful()) {
            return new DiagnosticCheck(
                    id,
                    name,
                    DiagnosticStatus.PASS,
                    expected,
                    actual,
                    "Runtime dependency is available.",
                    "No corrective action required.",
                    Map.of(
                            "exitCode",
                            result.exitCode(),
                            "durationMillis",
                            result.durationMillis(),
                            "critical",
                            critical
                    )
            );
        }

        DiagnosticStatus status = critical
                ? DiagnosticStatus.FAIL
                : DiagnosticStatus.WARN;

        return new DiagnosticCheck(
                id,
                name,
                status,
                expected,
                actual,
                "Runtime dependency is unavailable or not executable.",
                critical
                        ? "Install or restore "
                        + name
                        + " before running MAPAF."
                        : "Install "
                        + name
                        + " to enable all MAPAF capabilities.",
                Map.of(
                        "exitCode",
                        result.exitCode(),
                        "durationMillis",
                        result.durationMillis(),
                        "timedOut",
                        result.timedOut(),
                        "critical",
                        critical
                )
        );
    }

    private DiagnosticCheck repositoryWriteCheck(Path root) {
        Path checkFile = root.resolve(
                ".mapaf-doctor-write-check"
        );

        try {
            Files.writeString(
                    checkFile,
                    "MAPAF Doctor write validation"
            );

            Files.deleteIfExists(checkFile);

            return DiagnosticCheck.pass(
                    "repository-write-access",
                    "Repository Write Access",
                    "Repository root is writable",
                    "Writable"
            );

        } catch (Exception exception) {
            return DiagnosticCheck.fail(
                    "repository-write-access",
                    "Repository Write Access",
                    "Repository root is writable",
                    exception.getClass().getSimpleName(),
                    "MAPAF cannot write generated reports or evidence.",
                    "Grant write access to the MAPAF repository directory."
            );
        }
    }

    private DiagnosticCheck requiredDirectoryCheck(
            Path root,
            String relative,
            String name
    ) {
        boolean available = Files.isDirectory(
                root.resolve(relative)
        );

        return available
                ? DiagnosticCheck.pass(
                "directory-" + relative.replace('/', '-'),
                name,
                "Directory is present",
                relative
        )
                : DiagnosticCheck.fail(
                "directory-" + relative.replace('/', '-'),
                name,
                "Directory is present",
                "Missing: " + relative,
                "Required MAPAF directory is unavailable.",
                "Restore " + relative
                        + " from the verified release baseline."
        );
    }

    private DiagnosticCheck portCheck(int port) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(false);
            socket.bind(
                    new InetSocketAddress("127.0.0.1", port)
            );

            return DiagnosticCheck.pass(
                    "doctor-http-port",
                    "Doctor HTTP Port",
                    "Port " + port + " is available",
                    "Available"
            );

        } catch (Exception exception) {
            return new DiagnosticCheck(
                    "doctor-http-port",
                    "Doctor HTTP Port",
                    DiagnosticStatus.WARN,
                    "Port " + port + " is available",
                    "In use",
                    "The preferred Doctor HTTP port is already occupied.",
                    "Stop the existing service or set "
                            + "-Dmapaf.doctor.port to another port.",
                    Map.of("port", port)
            );
        }
    }

    private boolean isCriticalCheck(String id) {
        return switch (id) {
            case "java-runtime",
                 "gradle-wrapper",
                 "python-runtime",
                 "repository-write-access",
                 "directory-src-test-java",
                 "directory-scripts" -> true;
            default -> false;
        };
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "MAPAF runtime environment is fully operational.";
            case DEGRADED ->
                    "MAPAF runtime environment is operational with "
                            + "non-critical limitations.";
            case UNHEALTHY ->
                    "A critical MAPAF runtime dependency is unavailable.";
            case NOT_CONFIGURED ->
                    "MAPAF runtime environment is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }

    private String diagnosis(
            HealthStatus status,
            int criticalFailures,
            int warnings
    ) {
        return switch (status) {
            case HEALTHY ->
                    "No environment health issues detected.";
            case DEGRADED ->
                    warnings
                            + " non-critical environment issue(s) "
                            + "require attention.";
            case UNHEALTHY ->
                    criticalFailures
                            + " critical environment dependency "
                            + "failure(s) detected.";
            case NOT_CONFIGURED ->
                    "Environment diagnostics were not configured.";
        };
    }

    private String firstLine(String value) {
        String[] lines = value.split("\\R");
        return lines.length == 0
                ? value
                : lines[0].trim();
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
