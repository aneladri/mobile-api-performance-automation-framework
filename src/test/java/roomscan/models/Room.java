package roomscan.models;

import java.util.List;

public class Room {

    private final String name;
    private final double areaSqFt;
    private final double heightFt;
    private final List<Wall> walls;
    private final List<Door> doors;
    private final List<Window> windows;
    private final List<Furniture> furniture;

    public Room(
            String name,
            double areaSqFt,
            double heightFt,
            List<Wall> walls,
            List<Door> doors,
            List<Window> windows,
            List<Furniture> furniture
    ) {
        this.name = name;
        this.areaSqFt = areaSqFt;
        this.heightFt = heightFt;
        this.walls = walls;
        this.doors = doors;
        this.windows = windows;
        this.furniture = furniture;
    }

    public String getName() {
        return name;
    }

    public double getAreaSqFt() {
        return areaSqFt;
    }

    public double getHeightFt() {
        return heightFt;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Window> getWindows() {
        return windows;
    }

    public List<Furniture> getFurniture() {
        return furniture;
    }
}
