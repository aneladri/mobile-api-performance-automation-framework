package core.ai.analysis;

/**
 * Confidence level assigned to an AI failure analysis.
 */
public enum FailureConfidence {

    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN;

    public static FailureConfidence from(
            String value) {

        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }

        try {
            return FailureConfidence.valueOf(
                    value.trim().toUpperCase()
            );

        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
