package api.enterprise.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record CorrelationContext(String executionId, String correlationId) {

    public static CorrelationContext create() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return new CorrelationContext(
                "API-" + timestamp,
                UUID.randomUUID().toString()
        );
    }
}
