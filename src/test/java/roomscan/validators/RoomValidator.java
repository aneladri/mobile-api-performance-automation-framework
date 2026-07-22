package roomscan.validators;

import org.testng.Assert;
import roomscan.models.Room;

public final class RoomValidator {

    private RoomValidator() {
    }

    public static void assertName(
            Room room,
            String expectedName
    ) {
        Assert.assertEquals(
                room.getName(),
                expectedName,
                "Room name mismatch"
        );
    }

    public static void assertArea(
            Room room,
            double expectedArea,
            double tolerance
    ) {
        Assert.assertEquals(
                room.getAreaSqFt(),
                expectedArea,
                tolerance,
                "Room area mismatch"
        );
    }

    public static void assertHeight(
            Room room,
            double expectedHeight,
            double tolerance
    ) {
        Assert.assertEquals(
                room.getHeightFt(),
                expectedHeight,
                tolerance,
                "Room height mismatch"
        );
    }

    public static void assertWallCount(
            Room room,
            int expected
    ) {
        Assert.assertEquals(
                room.getWalls().size(),
                expected,
                "Unexpected wall count"
        );
    }

    public static void assertDoorCount(
            Room room,
            int expected
    ) {
        Assert.assertEquals(
                room.getDoors().size(),
                expected,
                "Unexpected door count"
        );
    }

    public static void assertWindowCount(
            Room room,
            int expected
    ) {
        Assert.assertEquals(
                room.getWindows().size(),
                expected,
                "Unexpected window count"
        );
    }

    public static void assertFurnitureCount(
            Room room,
            int expected
    ) {
        Assert.assertEquals(
                room.getFurniture().size(),
                expected,
                "Unexpected furniture count"
        );
    }
}
