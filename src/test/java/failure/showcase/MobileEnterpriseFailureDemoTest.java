package failure.showcase;
import org.testng.Assert;
import org.testng.annotations.Test;
public class MobileEnterpriseFailureDemoTest {
 @Test(description="Expected RoomScan mobile locator and healing failure")
 public void mobileUploadLocatorFailure() throws Exception {
   var r = FailureArtifactPublisher.publish("mobile", "Room image upload cannot continue", "Locator/Healing Failure", "Upload RoomScan Evidence", "Upload workflow started", "Upload control unavailable after 3 healing attempts", "Upload button accessibility ID changed and healing confidence remained below threshold.", "Review locator repository and update the upload screen accessibility contract.", false);
   Assert.fail("EXPECTED FAILURE SHOWCASE: " + r.path("actual").asText());
 }
}
