package roomscan.analysis;

import java.util.ArrayList;
import java.util.List;

public class RoomRequirementAnalyzer {

    public List<String> identifyCapabilities(String requirement) {

        List<String> capabilities =
                new ArrayList<>();

        String normalized =
                requirement == null
                        ? ""
                        : requirement.toLowerCase();

        if (normalized.contains("scan")) {
            capabilities.add("ROOM_SCAN");
        }

        if (normalized.contains("upload")) {
            capabilities.add("SCAN_UPLOAD");
        }

        if (normalized.contains("review")
                || normalized.contains("summary")) {
            capabilities.add("SCAN_REVIEW");
        }

        if (normalized.contains("pause")) {
            capabilities.add("SCAN_PAUSE");
        }

        if (normalized.contains("resume")) {
            capabilities.add("SCAN_RESUME");
        }

        if (normalized.contains("low light")) {
            capabilities.add("LOW_LIGHT_VALIDATION");
        }

        if (normalized.contains("mock")) {
            capabilities.add("MOCK_CAPTURE");
        }

        return capabilities;
    }
}
