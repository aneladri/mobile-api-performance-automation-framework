package enterprise.story;

public record BusinessMetric(
        String name,
        double value,
        String unit,
        String description
) {
    public BusinessMetric {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Metric name must not be blank");
        }
        unit = unit == null ? "" : unit;
        description = description == null ? "" : description;
    }
}
