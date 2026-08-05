package core.enterprise.execution;

import java.time.Duration;
import java.time.Instant;

public final class EnterpriseTimer {
    private final Instant startedAt = Instant.now();
    public long elapsedMillis() { return Duration.between(startedAt, Instant.now()).toMillis(); }
    public double elapsedSeconds() { return elapsedMillis() / 1000.0; }
    public Instant getStartedAt() { return startedAt; }
}
