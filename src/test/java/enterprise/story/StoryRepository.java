package enterprise.story;

import java.util.Map;
import java.util.Optional;

public final class StoryRepository {

    private final Map<String, BusinessScenario> scenarios;

    public StoryRepository() {
        BusinessScenario roomScan = RoomScanStory.endToEndScenario();
        this.scenarios = Map.of(roomScan.id(), roomScan);
    }

    public Optional<BusinessScenario> findById(String id) {
        return Optional.ofNullable(scenarios.get(id));
    }

    public BusinessScenario roomScan() {
        return scenarios.get("ROOMSCAN-E2E-001");
    }
}
