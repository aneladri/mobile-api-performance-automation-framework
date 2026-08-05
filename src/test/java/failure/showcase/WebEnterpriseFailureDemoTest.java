package failure.showcase;
import org.testng.Assert;
import org.testng.annotations.Test;
public class WebEnterpriseFailureDemoTest {
 @Test(description="Expected RoomScan portal approval failure")
 public void floorPlanApprovalFailure() throws Exception {
   var r = FailureArtifactPublisher.publish("web", "Floor plan approval request fails", "Product Defect", "Approve AI Floor Plan", "HTTP 200", "HTTP 500 from POST /api/scans/RS-1045/approve", "Portal received a backend approval failure; trace, video, network response and DOM snapshot were retained.", "Inspect approval-service logs using the correlation ID and retry after service recovery.", true);
   Assert.fail("EXPECTED FAILURE SHOWCASE: " + r.path("actual").asText());
 }
}
