package enterprise.story.tests;

import enterprise.story.BusinessScenario;
import enterprise.story.RoomScanStory;
import enterprise.story.StoryRepository;
import enterprise.story.WorkflowStage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RoomScanStoryTest {

    @Test
    public void shouldExposeCompleteRoomScanWorkflow() {
        BusinessScenario scenario = RoomScanStory.endToEndScenario();

        Assert.assertEquals(scenario.id(), "ROOMSCAN-E2E-001");
        Assert.assertEquals(scenario.workflow().transactions().size(), 11);
        Assert.assertEquals(
                scenario.workflow().transactions().get(0).stage(),
                WorkflowStage.AUTHENTICATE
        );
        Assert.assertEquals(
                scenario.workflow().transactions().get(10).stage(),
                WorkflowStage.RELEASE_DECISION
        );
    }

    @Test
    public void shouldExposeProductionScaleMetrics() {
        double targetRequests = RoomScanStory.productionMetrics().stream()
                .filter(metric -> "Target Request Volume".equals(metric.name()))
                .findFirst()
                .orElseThrow()
                .value();

        Assert.assertTrue(targetRequests >= 10000);
    }

    @Test
    public void shouldResolveRoomScanScenarioFromRepository() {
        StoryRepository repository = new StoryRepository();
        Assert.assertTrue(repository.findById("ROOMSCAN-E2E-001").isPresent());
    }

    @Test
    public void shouldProvideExecutiveReleaseRecommendation() {
        Assert.assertEquals(RoomScanStory.readyOutcome().recommendation(), "READY FOR PRODUCTION");
        Assert.assertTrue(RoomScanStory.readyOutcome().readinessScore() >= 90);
    }
}
