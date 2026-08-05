package dashboard.enterprise.release.gates;

/**
 * Contract implemented by every governed MAPAF quality gate.
 */
public interface QualityGateEvaluator {

    QualityGatePolicy policy();

    QualityGateResult evaluate(QualityGateContext context);
}
