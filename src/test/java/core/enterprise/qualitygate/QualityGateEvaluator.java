package core.enterprise.qualitygate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QualityGateEvaluator {
    private final List<QualityGateResult> results = new ArrayList<>();
    public void evaluate(String name, boolean passed, String expected, String actual, String message) {
        results.add(new QualityGateResult(name, passed, expected, actual, message));
    }
    public boolean passed() { return results.stream().allMatch(QualityGateResult::passed); }
    public List<QualityGateResult> getResults() {
        return Collections.unmodifiableList(new ArrayList<>(results));
    }
}
