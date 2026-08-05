package failure.showcase;
import org.testng.Assert;
import org.testng.annotations.Test;
public class ApiEnterpriseFailureDemoTest {
 @Test(description="Expected CubiCasa provider timeout")
 public void cubiCasaTimeoutFailure() throws Exception {
   var r = FailureArtifactPublisher.publish("api", "CubiCasa provider timeout", "External AI Provider Failure", "Submit AI Processing", "HTTP 202", "HTTP 504 AI_PROVIDER_TIMEOUT", "CubiCasa did not acknowledge the AI job within the provider timeout window.", "Apply retry with exponential backoff, preserve the scan session and alert provider operations.", true);
   Assert.fail("EXPECTED FAILURE SHOWCASE: " + r.path("actual").asText());
 }
}
