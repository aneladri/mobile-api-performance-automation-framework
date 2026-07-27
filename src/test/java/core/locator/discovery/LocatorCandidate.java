package core.locator.discovery;

public record LocatorCandidate(
        String logicalName,
        String elementClass,
        String locatorType,
        String locatorValue,
        int confidence,
        boolean unique,
        String source
) {
}
