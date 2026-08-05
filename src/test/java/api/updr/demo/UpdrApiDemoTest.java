package api.updr.demo;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class UpdrApiDemoTest {
    private static final String BASE_URL = System.getProperty("updr.baseUrl", "http://localhost:8090");
    private HttpClient client;
    @BeforeClass public void setUp(){client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();}
    @Test(priority=1) public void resetAndConfigureTemplate() throws Exception {
        assertStatus(post("/api/demo/reset","{}"),200,"\"reset\":true");
        assertStatus(post("/api/admin/templates","{\"templateId\":\"UPDR-RESIDENTIAL-V2\",\"name\":\"UPDR Enterprise Residential Inspection\",\"version\":\"2.0.0\"}"),201,"UPDR-RESIDENTIAL-V2");
        assertStatus(post("/api/admin/templates/UPDR-RESIDENTIAL-V2/assignments","{\"clientId\":\"CLIENT-001\"}"),201,"CLIENT-001");
    }
    @Test(priority=2) public void verifyOrderAndMlsPrefill() throws Exception { HttpResponse<String> r=get("/api/orders/UPDR-1001"); assertStatus(r,200,"MLS-88421"); Assert.assertTrue(r.body().contains("2150")); Assert.assertTrue(r.body().contains("UPDR-RESIDENTIAL-V2")); }
    @Test(priority=3) public void validateAiEvidenceAndOfflineRecovery() throws Exception {
        assertStatus(patch("/api/inspections/INSP-1001/sections/exterior","{\"damagePresent\":true,\"defectType\":\"STRUCTURAL_CRACK\",\"material\":\"BRICK\",\"severity\":\"HIGH\"}"),200,"IN_PROGRESS");
        assertStatus(post("/api/ai/photos/analyze","{}"),200,"\"qualityScore\":72");
        HttpResponse<String> duplicate=post("/api/ai/photos/analyze","{\"duplicate\":true}"); Assert.assertEquals(duplicate.statusCode(),409); Assert.assertTrue(duplicate.body().contains("\"duplicate\":true"));
        assertStatus(post("/api/inspections/INSP-1001/ai-confirmation","{}"),200,"\"confirmed\":true");
        assertStatus(post("/api/ai/voice/analyze","{}"),200,"WATER_DAMAGE");
        HttpResponse<String> failed=post("/api/inspections/INSP-1001/autosave","{\"failOnce\":true}"); Assert.assertEquals(failed.statusCode(),503); Assert.assertTrue(failed.body().contains("OFFLINE_PENDING_SYNC"));
        assertStatus(post("/api/inspections/INSP-1001/autosave","{}"),200,"SYNCED");
    }
    @Test(priority=4) public void demonstrateValidationFailureAndCorrection() throws Exception {
        HttpResponse<String> invalid=post("/api/inspections/INSP-1001/validate","{}"); Assert.assertEquals(invalid.statusCode(),422); Assert.assertTrue(invalid.body().contains("moistureSource")); Assert.assertTrue(invalid.body().contains("repairEstimate"));
        assertStatus(post("/api/ai/photos/analyze","{\"replacement\":true}"),200,"\"qualityScore\":96");
        assertStatus(patch("/api/inspections/INSP-1001/sections/exterior","{\"repairEstimate\":\"2500\"}"),200,"2500");
        assertStatus(patch("/api/inspections/INSP-1001/sections/interior","{\"moistureSource\":\"FAILED_WINDOW_SEAL\"}"),200,"FAILED_WINDOW_SEAL");
        assertStatus(post("/api/inspections/INSP-1001/validate","{}"),200,"READY_TO_SUBMIT");
        assertStatus(post("/api/inspections/INSP-1001/submit","{}"),202,"QUEUED_FOR_QC");
    }
    @Test(priority=5) public void demonstrateQcReturnCorrectionAndApproval() throws Exception {
        assertStatus(get("/api/qc/queue"),200,"INSP-1001");
        assertStatus(post("/api/qc/inspections/INSP-1001/return","{\"comment\":\"Provide a closer exterior-wall image and confirm repair estimate.\",\"section\":\"EXTERIOR\"}"),200,"RETURNED");
        assertStatus(post("/api/inspections/INSP-1001/submit","{}"),202,"RESUBMITTED");
        assertStatus(post("/api/qc/inspections/INSP-1001/approve","{}"),200,"APPROVED");
        assertStatus(get("/api/inspections/INSP-1001/audit"),200,"QC_APPROVED");
    }
    private void assertStatus(HttpResponse<String> r,int code,String token){Assert.assertEquals(r.statusCode(),code,r.body());Assert.assertTrue(r.body().contains(token),r.body());}
    private HttpResponse<String> get(String p)throws Exception{return send("GET",p,null);} private HttpResponse<String> post(String p,String b)throws Exception{return send("POST",p,b);} private HttpResponse<String> patch(String p,String b)throws Exception{return send("PATCH",p,b);}
    private HttpResponse<String> send(String m,String p,String b)throws Exception{HttpRequest.Builder x=HttpRequest.newBuilder(URI.create(BASE_URL+p)).timeout(Duration.ofSeconds(10)).header("Content-Type","application/json");x.method(m,b==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(b,StandardCharsets.UTF_8));return client.send(x.build(),HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));}
}
