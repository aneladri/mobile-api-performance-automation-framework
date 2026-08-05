package dashboard.enterprise.doctor.diagnosis.tests;

import dashboard.enterprise.doctor.diagnosis.engine.DefaultDoctorDiagnosisEngine;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisPriority;
import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public final class DoctorDiagnosisEngineTest {

    @Test
    public void shouldCreateDiagnosisContract() {
        var diagnosis = new DefaultDoctorDiagnosisEngine().diagnose(snapshot(degradedClaude()));
        Assert.assertEquals(diagnosis.schemaVersion(), "mapaf.doctor.diagnosis/v1");
        Assert.assertEquals(diagnosis.totalIssues(), 1);
        Assert.assertEquals(diagnosis.blockingIssues(), 0);
        Assert.assertEquals(diagnosis.diagnoses().get(0).owner(), "AI Platform Engineering");
    }

    @Test
    public void shouldPrioritizeBlockingIssue() {
        var diagnosis = new DefaultDoctorDiagnosisEngine().diagnose(snapshot(unhealthyRepository(), degradedClaude()));
        Assert.assertEquals(diagnosis.blockingIssues(), 1);
        Assert.assertEquals(diagnosis.diagnoses().get(0).priority(), DiagnosisPriority.P0_CRITICAL);
        Assert.assertTrue(diagnosis.diagnoses().get(0).blocking());
    }

    @Test
    public void shouldPublishNoIssuesForHealthySnapshot() {
        var diagnosis = new DefaultDoctorDiagnosisEngine().diagnose(snapshot(healthyApi()));
        Assert.assertEquals(diagnosis.totalIssues(), 0);
        Assert.assertTrue(diagnosis.immediateActions().isEmpty());
    }

    private DoctorSnapshot snapshot(HealthProbeResult... results) {
        List<HealthProbeResult> probes = List.of(results);
        int healthy = (int) probes.stream().filter(p -> p.status() == HealthStatus.HEALTHY).count();
        int degraded = (int) probes.stream().filter(p -> p.status() == HealthStatus.DEGRADED).count();
        int unhealthy = (int) probes.stream().filter(p -> p.status() == HealthStatus.UNHEALTHY).count();
        boolean ready = probes.stream().noneMatch(HealthProbeResult::blocksPlatformReadiness);
        HealthStatus overall = ready ? (degraded + unhealthy > 0 ? HealthStatus.DEGRADED : HealthStatus.HEALTHY) : HealthStatus.UNHEALTHY;
        return new DoctorSnapshot("mapaf.doctor/v1", "2026-08-02T00:00:00Z", "MAPAF Enterprise", "2.9.0", "TEST", overall, 90, ready, probes.size(), healthy, degraded, unhealthy, 0, "Test", List.of(), List.of(), probes);
    }

    private HealthProbeResult degradedClaude() {
        return new HealthProbeResult("claude-health", "Claude Health", "1.0", HealthSeverity.MEDIUM, HealthStatus.DEGRADED, 1, "Governed replay active.", "Live provider disabled.", List.of("Configure Claude credentials."), List.of(new DiagnosticCheck("claude-key", "Claude API Credential", dashboard.enterprise.doctor.model.DiagnosticStatus.WARN, "Configured", "Missing", "Credential is missing.", "Configure ANTHROPIC_API_KEY.", Map.of())), List.of("generated/ai/automation"), Map.of());
    }

    private HealthProbeResult unhealthyRepository() {
        return new HealthProbeResult("repository-foundation", "Repository Foundation", "1.0", HealthSeverity.CRITICAL, HealthStatus.UNHEALTHY, 1, "Repository incomplete.", "gradlew is missing.", List.of("Restore gradlew."), List.of(), List.of(), Map.of());
    }

    private HealthProbeResult healthyApi() {
        return new HealthProbeResult("api-health", "API Health", "1.0", HealthSeverity.CRITICAL, HealthStatus.HEALTHY, 1, "API healthy.", "No issue detected.", List.of(), List.of(), List.of(), Map.of());
    }
}
