package dashboard.enterprise.doctor.diagnosis.engine;

import dashboard.enterprise.doctor.diagnosis.model.CorrectiveActionStatus;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisItem;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisPriority;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisSnapshot;
import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DiagnosticStatus;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultDoctorDiagnosisEngine implements DoctorDiagnosisEngine {

    @Override
    public DiagnosisSnapshot diagnose(DoctorSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Doctor snapshot is required.");
        }

        List<DiagnosisItem> items = new ArrayList<>();
        AtomicInteger sequence = new AtomicInteger(1);

        for (HealthProbeResult probe : snapshot.probeResults()) {
            List<DiagnosticCheck> failingChecks = probe.checks().stream()
                    .filter(check -> check.status() == DiagnosticStatus.FAIL || check.status() == DiagnosticStatus.WARN)
                    .toList();

            if (failingChecks.isEmpty() && probe.status() != HealthStatus.HEALTHY) {
                items.add(fromProbe(sequence.getAndIncrement(), probe));
            } else {
                for (DiagnosticCheck check : failingChecks) {
                    items.add(fromCheck(sequence.getAndIncrement(), probe, check));
                }
            }
        }

        items = items.stream()
                .sorted((left, right) -> Integer.compare(rank(left.priority()), rank(right.priority())))
                .toList();

        int blocking = (int) items.stream().filter(DiagnosisItem::blocking).count();
        int warnings = items.size() - blocking;
        int open = (int) items.stream().filter(item -> item.actionStatus() == CorrectiveActionStatus.OPEN).count();

        Map<String, String> impact = new LinkedHashMap<>();
        for (HealthProbeResult probe : snapshot.probeResults()) {
            impact.put(probe.probeName(), impactText(probe.status()));
        }

        List<String> immediate = items.stream()
                .filter(item -> item.priority() == DiagnosisPriority.P0_CRITICAL || item.priority() == DiagnosisPriority.P1_HIGH)
                .map(DiagnosisItem::recommendedAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .limit(5)
                .toList();

        String summary = blocking > 0
                ? blocking + " blocking issue(s) require immediate action before reliable platform execution."
                : items.isEmpty()
                ? "No corrective actions are required. All platform capabilities are healthy."
                : items.size() + " non-blocking issue(s) require planned remediation while the platform remains operational.";

        return new DiagnosisSnapshot(
                "mapaf.doctor.diagnosis/v1",
                snapshot.generatedAt(),
                snapshot.product(),
                snapshot.platformVersion(),
                snapshot.environment(),
                items.size(),
                blocking,
                warnings,
                open,
                summary,
                items,
                impact,
                immediate,
                "doctor-overview.html",
                "doctor-report.html"
        );
    }

    private DiagnosisItem fromProbe(int sequence, HealthProbeResult probe) {
        return new DiagnosisItem(
                id(sequence),
                probe.probeId(),
                probe.probeName(),
                probe.status(),
                probe.severity(),
                priority(probe.severity(), probe.blocksPlatformReadiness()),
                probe.blocksPlatformReadiness(),
                probe.summary(),
                probe.diagnosis(),
                technicalImpact(probe.probeId()),
                businessImpact(probe.probeId()),
                firstAction(probe.correctiveActions()),
                owner(probe.probeId()),
                effort(probe.severity()),
                CorrectiveActionStatus.OPEN,
                firstEvidence(probe.evidenceReferences())
        );
    }

    private DiagnosisItem fromCheck(int sequence, HealthProbeResult probe, DiagnosticCheck check) {
        boolean blocking = probe.blocksPlatformReadiness()
                || (check.status() == DiagnosticStatus.FAIL && probe.severity() == HealthSeverity.CRITICAL);

        return new DiagnosisItem(
                id(sequence),
                probe.probeId(),
                probe.probeName(),
                probe.status(),
                probe.severity(),
                priority(probe.severity(), blocking),
                blocking,
                check.name() + " — " + check.actual(),
                check.diagnosis(),
                technicalImpact(probe.probeId()),
                businessImpact(probe.probeId()),
                check.correctiveAction(),
                owner(probe.probeId()),
                effort(probe.severity()),
                CorrectiveActionStatus.OPEN,
                firstEvidence(probe.evidenceReferences())
        );
    }

    private DiagnosisPriority priority(HealthSeverity severity, boolean blocking) {
        if (blocking || severity == HealthSeverity.CRITICAL) return DiagnosisPriority.P0_CRITICAL;
        if (severity == HealthSeverity.HIGH) return DiagnosisPriority.P1_HIGH;
        if (severity == HealthSeverity.MEDIUM) return DiagnosisPriority.P2_MEDIUM;
        return DiagnosisPriority.P3_LOW;
    }

    private int rank(DiagnosisPriority priority) {
        return switch (priority) {
            case P0_CRITICAL -> 0;
            case P1_HIGH -> 1;
            case P2_MEDIUM -> 2;
            case P3_LOW -> 3;
        };
    }

    private String owner(String probeId) {
        if (probeId.contains("claude")) return "AI Platform Engineering";
        if (probeId.contains("device")) return "Mobile Quality Engineering";
        if (probeId.contains("api")) return "API Platform Engineering";
        if (probeId.contains("performance")) return "Performance Engineering";
        if (probeId.contains("dashboard")) return "Quality Platform Engineering";
        if (probeId.contains("browser")) return "Web Quality Engineering";
        return "Platform Engineering";
    }

    private String effort(HealthSeverity severity) {
        return switch (severity) {
            case CRITICAL -> "1-4 hours";
            case HIGH -> "30-90 minutes";
            case MEDIUM -> "10-30 minutes";
            case LOW, INFO -> "Under 15 minutes";
        };
    }

    private String technicalImpact(String probeId) {
        if (probeId.contains("claude")) return "Live AI generation may be unavailable; governed replay can continue when assets exist.";
        if (probeId.contains("device")) return "Mobile execution targets or Appium capabilities may be partially unavailable.";
        if (probeId.contains("api")) return "API health validation or API automation evidence may be incomplete.";
        if (probeId.contains("performance")) return "Performance execution, SLA validation, or production evidence may be incomplete.";
        if (probeId.contains("dashboard")) return "Executive reporting or report navigation may be incomplete.";
        if (probeId.contains("browser")) return "Browser automation or browser evidence generation may be unavailable.";
        if (probeId.contains("environment")) return "One or more runtime dependencies may limit platform execution.";
        return "Platform reliability may be reduced until the issue is remediated.";
    }

    private String businessImpact(String probeId) {
        if (probeId.contains("claude")) return "New AI-generated automation may require governed replay or manual generation.";
        if (probeId.contains("device")) return "Mobile release coverage may be reduced for unavailable device families.";
        if (probeId.contains("api")) return "API release confidence may be reduced until evidence or connectivity is restored.";
        if (probeId.contains("performance")) return "Capacity and SLA confidence may be reduced until performance evidence is restored.";
        if (probeId.contains("dashboard")) return "Stakeholders may not have complete operational visibility.";
        return "Delivery teams may experience reduced automation reliability or slower troubleshooting.";
    }

    private String impactText(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> "Ready";
            case DEGRADED -> "Operational with limitations";
            case UNHEALTHY -> "Unavailable or unreliable";
            case NOT_CONFIGURED -> "Not configured";
        };
    }

    private String id(int value) { return String.format("DIAG-%03d", value); }
    private String firstAction(List<String> values) { return values == null || values.isEmpty() ? "Review the detailed probe diagnostics." : values.get(0); }
    private String firstEvidence(List<String> values) { return values == null || values.isEmpty() ? "" : values.get(0); }
}
