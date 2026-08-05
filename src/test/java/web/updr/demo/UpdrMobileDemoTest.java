package web.updr.demo;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.config.WebConfiguration;
import web.tests.base.BaseWebTest;
import com.microsoft.playwright.options.RequestOptions;

public class UpdrMobileDemoTest extends BaseWebTest {
 private static final String BASE_URL=System.getProperty("updr.baseUrl","http://localhost:8090");
 @Override protected WebConfiguration createWebConfiguration(){WebConfiguration c=super.createWebConfiguration();c.setTraceEnabled(true);c.setSaveTraceOnSuccess(true);c.setVideoEnabled(true);return c;}
 @Test(description="UPDR Inspector mobile-emulated AI and resilience demonstration") public void demonstrateInspectorMobileJourney(){
  page().request().post(BASE_URL+"/api/demo/reset");
  page().request().post(BASE_URL+"/api/admin/templates",RequestOptions.create().setData("{\"templateId\":\"UPDR-RESIDENTIAL-V2\",\"name\":\"UPDR Enterprise Residential Inspection\"}"));
  page().request().post(BASE_URL+"/api/admin/templates/UPDR-RESIDENTIAL-V2/assignments",RequestOptions.create().setData("{\"clientId\":\"CLIENT-001\"}"));
  try(BrowserContext mobile=browser().newContext(new Browser.NewContextOptions().setViewportSize(390,844).setIsMobile(true).setHasTouch(true))){Page p=mobile.newPage();p.navigate(BASE_URL+"/inspector.html");p.getByTestId("load-order").click();p.waitForCondition(()->p.getByTestId("order-id").innerText().contains("UPDR-1001"));p.getByTestId("damage-present").selectOption("YES");p.getByTestId("analyze-photo").click();p.waitForCondition(()->p.getByTestId("quality-score").innerText().equals("72"));p.getByTestId("confirm-ai").click();p.getByTestId("analyze-voice").click();p.getByTestId("autosave-fail").click();p.waitForCondition(()->p.getByTestId("sync-status").innerText().contains("OFFLINE_PENDING_SYNC"));p.getByTestId("autosave-retry").click();p.waitForCondition(()->p.getByTestId("sync-status").innerText().contains("SYNCED"));Assert.assertTrue(p.getByTestId("property-summary").innerText().contains("MLS-88421"));}
 }
}
