package dashboard.enterprise.doctor.probe.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.environment.CommandExecutor;
import dashboard.enterprise.doctor.probe.environment.CommandResult;
import dashboard.enterprise.doctor.probe.environment.ProcessCommandExecutor;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SystemDeviceInspector implements DeviceInspector {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    private static final Duration COMMAND_TIMEOUT =
            Duration.ofSeconds(10);

    private final CommandExecutor commandExecutor;

    public SystemDeviceInspector() {
        this(new ProcessCommandExecutor());
    }

    public SystemDeviceInspector(
            CommandExecutor commandExecutor
    ) {
        if (commandExecutor == null) {
            throw new IllegalArgumentException(
                    "Command executor is required."
            );
        }

        this.commandExecutor = commandExecutor;
    }

    @Override
    public DeviceInventoryResult inspect(
            DoctorContext context
    ) {
        long started = System.nanoTime();

        String operatingSystem = System.getProperty(
                "os.name",
                "UNKNOWN"
        );

        String executionMode = firstNonBlank(
                context.environmentVariable(
                        "MAPAF_DEVICE_EXECUTION_MODE"
                ),
                System.getProperty(
                        "mapaf.device.execution.mode"
                ),
                "OPTIONAL"
        ).toUpperCase(Locale.ROOT);

        CommandResult adbVersion = commandExecutor.execute(
                List.of("adb", "version"),
                COMMAND_TIMEOUT
        );

        boolean adbAvailable = adbVersion.successful();

        List<String> androidDeviceIds = new ArrayList<>();
        int androidDevices = 0;
        int authorized = 0;
        int unauthorized = 0;

        if (adbAvailable) {
            CommandResult devices = commandExecutor.execute(
                    List.of("adb", "devices"),
                    COMMAND_TIMEOUT
            );

            if (devices.successful()) {
                for (String line :
                        devices.standardOutput().split("\\R")) {

                    String trimmed = line.trim();

                    if (trimmed.isBlank()
                            || trimmed.startsWith(
                            "List of devices attached"
                    )
                            || !trimmed.contains("\t")) {
                        continue;
                    }

                    String[] parts = trimmed.split("\\s+");

                    if (parts.length < 2) {
                        continue;
                    }

                    androidDevices++;

                    String id = parts[0];
                    String state = parts[1];

                    androidDeviceIds.add(id);

                    if ("device".equalsIgnoreCase(state)) {
                        authorized++;
                    } else {
                        unauthorized++;
                    }
                }
            }
        }

        CommandResult appiumVersion = commandExecutor.execute(
                List.of("appium", "--version"),
                COMMAND_TIMEOUT
        );

        boolean appiumAvailable =
                appiumVersion.successful();

        boolean appiumServerCheckEnabled =
                Boolean.parseBoolean(
                        System.getProperty(
                                "mapaf.device.appium.server.check.enabled",
                                "false"
                        )
                );

        String appiumEndpoint = System.getProperty(
                "mapaf.device.appium.url",
                "http://127.0.0.1:4723/status"
        );

        boolean appiumReachable =
                !appiumServerCheckEnabled
                        || checkAppium(appiumEndpoint);

        boolean macOs = operatingSystem
                .toLowerCase(Locale.ROOT)
                .contains("mac");

        boolean simctlAvailable = false;
        int iosBootedSimulators = 0;
        List<String> simulatorIds = new ArrayList<>();

        if (macOs) {
            CommandResult simctlHelp = commandExecutor.execute(
                    List.of(
                            "xcrun",
                            "simctl",
                            "help"
                    ),
                    COMMAND_TIMEOUT
            );

            simctlAvailable = simctlHelp.successful();

            if (simctlAvailable) {
                CommandResult simctlDevices =
                        commandExecutor.execute(
                                List.of(
                                        "xcrun",
                                        "simctl",
                                        "list",
                                        "devices",
                                        "booted",
                                        "--json"
                                ),
                                COMMAND_TIMEOUT
                        );

                if (simctlDevices.successful()) {
                    simulatorIds.addAll(
                            parseBootedSimulators(
                                    simctlDevices.standardOutput()
                            )
                    );

                    iosBootedSimulators =
                            simulatorIds.size();
                }
            }
        }

        Path mobileEvidence = context.repositoryRoot()
                .resolve("mobile/reports");

        boolean mobileEvidenceAvailable =
                Files.isDirectory(mobileEvidence);

        List<String> evidence = new ArrayList<>();

        if (mobileEvidenceAvailable) {
            evidence.add("mobile/reports");
        }

        Map<String, Object> metadata =
                new LinkedHashMap<>();

        metadata.put("adbOutput",
                firstLine(adbVersion.combinedOutput()));
        metadata.put("appiumOutput",
                firstLine(appiumVersion.combinedOutput()));
        metadata.put("macOs", macOs);
        metadata.put("executionMode", executionMode);

        String diagnosis = diagnosis(
                executionMode,
                adbAvailable,
                authorized,
                appiumAvailable,
                appiumServerCheckEnabled,
                appiumReachable,
                simctlAvailable,
                iosBootedSimulators
        );

        return new DeviceInventoryResult(
                executionMode,
                adbAvailable,
                androidDevices,
                authorized,
                unauthorized,
                appiumAvailable,
                appiumServerCheckEnabled,
                appiumReachable,
                appiumEndpoint,
                simctlAvailable,
                iosBootedSimulators,
                mobileEvidenceAvailable,
                operatingSystem,
                diagnosis,
                elapsedMillis(started),
                androidDeviceIds,
                simulatorIds,
                evidence,
                metadata
        );
    }

    private List<String> parseBootedSimulators(
            String json
    ) {
        List<String> ids = new ArrayList<>();

        if (json == null || json.isBlank()) {
            return ids;
        }

        try {
            JsonNode root = MAPPER.readTree(json);
            JsonNode devices = root.get("devices");

            if (devices == null || !devices.isObject()) {
                return ids;
            }

            devices.fields().forEachRemaining(entry -> {
                JsonNode runtimeDevices = entry.getValue();

                if (!runtimeDevices.isArray()) {
                    return;
                }

                for (JsonNode device : runtimeDevices) {
                    String state = device.path("state").asText();
                    boolean available = device.path("isAvailable")
                            .asBoolean(true);

                    if ("Booted".equalsIgnoreCase(state)
                            && available) {
                        String udid =
                                device.path("udid").asText();

                        if (!udid.isBlank()) {
                            ids.add(udid);
                        }
                    }
                }
            });

        } catch (Exception ignored) {
            return List.of();
        }

        return ids;
    }

    private boolean checkAppium(String endpoint) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)
                    URI.create(endpoint)
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);

            int responseCode =
                    connection.getResponseCode();

            return responseCode >= 200
                    && responseCode < 400;

        } catch (Exception exception) {
            return false;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String diagnosis(
            String executionMode,
            boolean adbAvailable,
            int androidAuthorized,
            boolean appiumAvailable,
            boolean appiumCheckEnabled,
            boolean appiumReachable,
            boolean simctlAvailable,
            int iosBooted
    ) {
        boolean androidReady =
                adbAvailable && androidAuthorized > 0;

        boolean iosReady =
                simctlAvailable && iosBooted > 0;

        boolean appiumReady =
                appiumAvailable
                        && (!appiumCheckEnabled
                        || appiumReachable);

        return switch (executionMode) {
            case "ANDROID_REQUIRED" ->
                    androidReady && appiumReady
                            ? "Android device execution is ready."
                            : "Android device execution is not ready.";

            case "IOS_REQUIRED" ->
                    iosReady && appiumReady
                            ? "iOS simulator execution is ready."
                            : "iOS simulator execution is not ready.";

            case "ANY_DEVICE_REQUIRED" ->
                    (androidReady || iosReady)
                            && appiumReady
                            ? "At least one mobile execution target is ready."
                            : "No usable mobile execution target is ready.";

            default ->
                    androidReady || iosReady
                            ? "Mobile execution capability is available."
                            : "No active mobile device is connected; "
                            + "mobile execution remains optional.";
        };
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    private String firstLine(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String[] lines = value.split("\\R");
        return lines.length == 0
                ? value.trim()
                : lines[0].trim();
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
