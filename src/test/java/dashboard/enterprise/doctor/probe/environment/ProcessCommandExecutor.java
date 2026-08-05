package dashboard.enterprise.doctor.probe.environment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ProcessCommandExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(
            List<String> command,
            Duration timeout
    ) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException(
                    "Command is required."
            );
        }

        if (timeout == null
                || timeout.isZero()
                || timeout.isNegative()) {
            throw new IllegalArgumentException(
                    "Positive command timeout is required."
            );
        }

        long started = System.nanoTime();

        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(false)
                    .start();

            boolean completed = process.waitFor(
                    timeout.toMillis(),
                    TimeUnit.MILLISECONDS
            );

            if (!completed) {
                process.destroyForcibly();

                return new CommandResult(
                        -1,
                        "",
                        "Command timed out.",
                        elapsedMillis(started),
                        true
                );
            }

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String error = new String(
                    process.getErrorStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return new CommandResult(
                    process.exitValue(),
                    output,
                    error,
                    elapsedMillis(started),
                    false
            );

        } catch (IOException exception) {
            return new CommandResult(
                    -1,
                    "",
                    exception.getMessage(),
                    elapsedMillis(started),
                    false
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            return new CommandResult(
                    -1,
                    "",
                    "Command execution was interrupted.",
                    elapsedMillis(started),
                    false
            );
        }
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
