package roomscan.models;

public class Furniture {

    private final String id;
    private final String type;
    private final double widthFt;
    private final double depthFt;
    private final double heightFt;

    public Furniture(
            String id,
            String type,
            double widthFt,
            double depthFt,
            double heightFt
    ) {
        this.id = id;
        this.type = type;
        this.widthFt = widthFt;
        this.depthFt = depthFt;
        this.heightFt = heightFt;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getWidthFt() {
        return widthFt;
    }

    public double getDepthFt() {
        return depthFt;
    }

    public double getHeightFt() {
        return heightFt;
    }
}
