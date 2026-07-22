package roomscan.models;

public class Window {

    private final String id;
    private final double widthFt;
    private final double heightFt;
    private final String wallId;

    public Window(
            String id,
            double widthFt,
            double heightFt,
            String wallId
    ) {
        this.id = id;
        this.widthFt = widthFt;
        this.heightFt = heightFt;
        this.wallId = wallId;
    }

    public String getId() {
        return id;
    }

    public double getWidthFt() {
        return widthFt;
    }

    public double getHeightFt() {
        return heightFt;
    }

    public String getWallId() {
        return wallId;
    }
}
