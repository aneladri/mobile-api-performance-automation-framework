package failure.showcase;
import org.testng.Assert;
import org.testng.annotations.Test;
public class PerformanceEnterpriseFailureDemoTest {
 @Test(description="Expected RoomScan performance SLA violation")
 public void uploadEndpointSaturation() throws Exception {
   var r = FailureArtifactPublisher.publish("performance", "Room image upload saturation", "Performance SLA Violation", "Upload Room Images", "P95 < 500 ms; error rate < 1%; availability >= 99%", "P95 870 ms; error rate 3.20%; availability 96.80%", "Upload workers and object-storage connections saturated under the production transaction mix.", "Scale upload workers and investigate object-storage connection pooling.", true);
   Assert.fail("EXPECTED FAILURE SHOWCASE: " + r.path("actual").asText());
 }
}
