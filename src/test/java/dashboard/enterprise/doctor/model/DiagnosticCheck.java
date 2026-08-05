package dashboard.enterprise.doctor.model;

import java.util.Map;

/**
 * Auditable result of one low-level platform diagnostic.
 */
public record DiagnosticCheck(
        String id,
        String name,
        DiagnosticStatus status,
        String expected,
        String actual,
        String diagnosis,
        String correctiveAction,
        Map<String, Object> measurements
) {

    public DiagnosticCheck {
        require(id, "Diagnostic check id");
        require(name, "Diagnostic check name");

        if (status == null) {
            throw new IllegalArgumentException(
                    "Diagnostic check status is required."
            );
        }

        expected = safe(expected);
        actual = safe(actual);
        diagnosis = safe(diagnosis);
        correctiveAction = safe(correctiveAction);

        measurements = measurements == null
                ? Map.of()
                : Map.copyOf(measurements);
    }

    public static DiagnosticCheck pass(
            String id,
            String name,
            String expected,
            String actual
    ) {
        return new DiagnosticCheck(
                id,
                name,
                DiagnosticStatus.PASS,
                expected,
                actual,
                "No issue detected.",
                "No corrective action required.",
                Map.of()
        );
    }

    public static DiagnosticCheck fail(
            String id,
            String name,
            String expected,
            String actual,
            String diagnosis,
            String correctiveAction
    ) {
        return new DiagnosticCheck(
                id,
                name,
                DiagnosticStatus.FAIL,
                expected,
                actual,
                diagnosis,
                correctiveAction,
                Map.of()
        );
    }

    private static void require(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
