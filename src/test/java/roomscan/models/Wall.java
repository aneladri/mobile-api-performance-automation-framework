package roomscan.models;

public class Wall {

    private final String id;
    private final double lengthFt;
    private final double heightFt;
    private final double confidence;

    public Wall(
            String id,
            double lengthFt,
            double heightFt,
            double confidence
    ) {
        this.id = id;
        this.lengthFt = lengthFt;
        this.heightFt = heightFt;
        this.confidence = confidence;
    }

    public String getId() {
        return id;
    }

    public double getLengthFt() {
        return lengthFt;
    }

    public double getHeightFt() {
        return heightFt;
    }

    public double getConfidence() {
        return confidence;
    }
}
