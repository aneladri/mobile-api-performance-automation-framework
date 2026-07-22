package roomscan.fixtures;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ScanFixtureManager {

    private static final Path FIXTURE_ROOT =
            Path.of(
                    "src",
                    "test",
                    "resources",
                    "roomscan",
                    "fixtures"
            );

    public static final String SMALL_BEDROOM_CLEAN =
            "small_bedroom_clean";

    public static final String LARGE_LIVING_ROOM_OBSTACLES =
            "large_living_room_obstacles";

    public static final String MULTI_ROOM_APARTMENT =
            "multi_room_apartment";

    public static final String LOW_LIGHT_WARNING_SCAN =
            "low_light_warning_scan";

    public static final String INCOMPLETE_COVERAGE_SCAN =
            "incomplete_coverage_scan";

    public static final String CORRUPT_SCAN_DATA =
            "corrupt_scan_data";

    private ScanFixtureManager() {
    }

    public static Path getFixturePath(String fixtureName) {

        Path fixturePath =
                FIXTURE_ROOT.resolve(fixtureName);

        if (!Files.exists(fixturePath)) {
            throw new IllegalArgumentException(
                    "Unknown scan fixture: " + fixtureName
            );
        }

        return fixturePath;
    }

    public static Map<String, String> buildLaunchArgs(
            String fixtureName
    ) {
        return Map.of(
                "capture_provider",
                "mock",
                "scan_fixture",
                fixtureName
        );
    }

    public static boolean exists(String fixtureName) {
        return Files.exists(
                FIXTURE_ROOT.resolve(fixtureName)
        );
    }
}
