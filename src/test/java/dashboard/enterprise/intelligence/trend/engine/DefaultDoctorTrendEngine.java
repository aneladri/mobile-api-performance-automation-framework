package dashboard.enterprise.intelligence.trend.engine;

import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.model.DoctorHistoryRecord;
import dashboard.enterprise.intelligence.trend.model.ReleaseTrendSummary;
import dashboard.enterprise.intelligence.trend.model.TrendDirection;
import dashboard.enterprise.intelligence.trend.model.TrendPoint;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DefaultDoctorTrendEngine implements DoctorTrendEngine {

    @Override
    public TrendSnapshot analyze(DoctorHistoryIndex index) {
        if (index == null) {
            throw new IllegalArgumentException("Doctor history index is required.");
        }

        List<DoctorHistoryRecord> records = index.records();
        if (records.isEmpty()) {
            return empty();
        }

        DoctorHistoryRecord current = records.get(0);
        DoctorHistoryRecord previous = records.size() > 1 ? records.get(1) : null;

        int scoreDelta = previous == null ? 0 : current.healthScore() - previous.healthScore();
        int healthyDelta = previous == null ? 0 : current.healthyProbes() - previous.healthyProbes();
        int degradedDelta = previous == null ? 0 : current.degradedProbes() - previous.degradedProbes();
        int unhealthyDelta = previous == null ? 0 : current.unhealthyProbes() - previous.unhealthyProbes();

        List<String> regressions = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        if (previous != null) {
            if (scoreDelta < 0) regressions.add("Health score decreased by " + Math.abs(scoreDelta) + " point(s).");
            if (scoreDelta > 0) improvements.add("Health score increased by " + scoreDelta + " point(s).");
            if (healthyDelta < 0) regressions.add("Healthy probes decreased by " + Math.abs(healthyDelta) + ".");
            if (healthyDelta > 0) improvements.add("Healthy probes increased by " + healthyDelta + ".");
            if (degradedDelta > 0) regressions.add("Degraded probes increased by " + degradedDelta + ".");
            if (degradedDelta < 0) improvements.add("Degraded probes decreased by " + Math.abs(degradedDelta) + ".");
            if (unhealthyDelta > 0) regressions.add("Unhealthy probes increased by " + unhealthyDelta + ".");
            if (unhealthyDelta < 0) improvements.add("Unhealthy probes decreased by " + Math.abs(unhealthyDelta) + ".");

            Set<String> oldDiagnoses = new LinkedHashSet<>(previous.platformDiagnoses());
            Set<String> newDiagnoses = new LinkedHashSet<>(current.platformDiagnoses());
            for (String diagnosis : newDiagnoses) {
                if (!oldDiagnoses.contains(diagnosis)) regressions.add("New diagnosis: " + diagnosis);
            }
            for (String diagnosis : oldDiagnoses) {
                if (!newDiagnoses.contains(diagnosis)) improvements.add("Resolved diagnosis: " + diagnosis);
            }
        }

        boolean readinessRegressed = previous != null && previous.platformReady() && !current.platformReady();
        boolean readinessRecovered = previous != null && !previous.platformReady() && current.platformReady();
        if (readinessRegressed) regressions.add("Platform readiness changed from ready to not ready.");
        if (readinessRecovered) improvements.add("Platform readiness recovered.");

        TrendDirection direction = classify(records, scoreDelta, readinessRegressed, readinessRecovered);

        return new TrendSnapshot(
                "mapaf.intelligence.trend/v1",
                Instant.now().toString(),
                records.size(),
                current.recordId(),
                previous == null ? "" : previous.recordId(),
                current.healthScore(),
                previous == null ? current.healthScore() : previous.healthScore(),
                scoreDelta,
                direction,
                current.platformReady(),
                readinessRegressed,
                readinessRecovered,
                current.healthyProbes(),
                healthyDelta,
                current.degradedProbes(),
                degradedDelta,
                current.unhealthyProbes(),
                unhealthyDelta,
                regressions,
                improvements,
                current.platformDiagnoses(),
                timeline(records),
                releaseSummaries(records)
        );
    }

    private TrendSnapshot empty() {
        return new TrendSnapshot(
                "mapaf.intelligence.trend/v1",
                Instant.now().toString(),
                0, "", "", 0, 0, 0,
                TrendDirection.INSUFFICIENT_DATA,
                false, false, false,
                0, 0, 0, 0, 0, 0,
                List.of(), List.of(), List.of(), List.of(), List.of()
        );
    }

    private TrendDirection classify(
            List<DoctorHistoryRecord> records,
            int scoreDelta,
            boolean readinessRegressed,
            boolean readinessRecovered
    ) {
        if (records.size() < 2) return TrendDirection.INSUFFICIENT_DATA;
        if (readinessRegressed || scoreDelta <= -3) return TrendDirection.DECLINING;
        if (readinessRecovered || scoreDelta >= 3) return TrendDirection.IMPROVING;
        return TrendDirection.STABLE;
    }

    private List<TrendPoint> timeline(List<DoctorHistoryRecord> records) {
        return records.stream()
                .map(record -> new TrendPoint(
                        record.recordId(), record.capturedAt(), record.releaseId(),
                        record.executionId(), record.overallStatus(), record.healthScore(),
                        record.platformReady(), record.healthyProbes(), record.degradedProbes(),
                        record.unhealthyProbes(), record.notConfiguredProbes()
                ))
                .toList();
    }

    private List<ReleaseTrendSummary> releaseSummaries(List<DoctorHistoryRecord> records) {
        Map<String, List<DoctorHistoryRecord>> grouped = new LinkedHashMap<>();
        for (DoctorHistoryRecord record : records) {
            grouped.computeIfAbsent(record.releaseId(), ignored -> new ArrayList<>()).add(record);
        }

        List<ReleaseTrendSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, List<DoctorHistoryRecord>> entry : grouped.entrySet()) {
            List<DoctorHistoryRecord> releaseRecords = entry.getValue();
            DoctorHistoryRecord latest = releaseRecords.get(0);
            int min = releaseRecords.stream().mapToInt(DoctorHistoryRecord::healthScore).min().orElse(0);
            int max = releaseRecords.stream().mapToInt(DoctorHistoryRecord::healthScore).max().orElse(0);
            double average = releaseRecords.stream().mapToInt(DoctorHistoryRecord::healthScore).average().orElse(0.0);
            TrendDirection direction = releaseRecords.size() < 2
                    ? TrendDirection.INSUFFICIENT_DATA
                    : classify(releaseRecords,
                    releaseRecords.get(0).healthScore() - releaseRecords.get(1).healthScore(),
                    releaseRecords.get(1).platformReady() && !releaseRecords.get(0).platformReady(),
                    !releaseRecords.get(1).platformReady() && releaseRecords.get(0).platformReady());
            summaries.add(new ReleaseTrendSummary(
                    entry.getKey(), releaseRecords.size(), latest.healthScore(), average,
                    min, max, latest.platformReady(), direction
            ));
        }
        return summaries;
    }
}
