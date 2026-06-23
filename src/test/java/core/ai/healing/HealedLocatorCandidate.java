package core.ai.healing;

public class HealedLocatorCandidate {

    private final String locatorType;
    private final String locatorValue;
    private final int confidence;
    private final String explanation;

    public HealedLocatorCandidate(
            String locatorType,
            String locatorValue,
            int confidence,
            String explanation
    ) {
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
        this.confidence = confidence;
        this.explanation = explanation;
    }

    public String getLocatorType() {
        return locatorType;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return locatorType + ":" + locatorValue
                + " (" + confidence + "%)";
    }
}
