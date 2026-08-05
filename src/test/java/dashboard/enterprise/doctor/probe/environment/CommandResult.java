package dashboard.enterprise.doctor.probe.environment;

public record CommandResult(
        int exitCode,
        String standardOutput,
        String standardError,
        long durationMillis,
        boolean timedOut
) {

    public CommandResult {
        standardOutput = standardOutput == null
                ? ""
                : standardOutput.trim();

        standardError = standardError == null
                ? ""
                : standardError.trim();

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Command duration cannot be negative."
            );
        }
    }

    public boolean successful() {
        return exitCode == 0 && !timedOut;
    }

    public String combinedOutput() {
        if (standardOutput.isBlank()) {
            return standardError;
        }

        if (standardError.isBlank()) {
            return standardOutput;
        }

        return standardOutput + System.lineSeparator()
                + standardError;
    }
}
