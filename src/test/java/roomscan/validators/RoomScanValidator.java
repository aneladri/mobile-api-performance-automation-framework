package roomscan.validators;

import org.testng.Assert;
import roomscan.models.RoomScan;

public final class RoomScanValidator {

    private RoomScanValidator() {
    }

    public static void assertCompleted(
            RoomScan roomScan
    ) {
        Assert.assertEquals(
                roomScan.getStatus(),
                "COMPLETED",
                "Room scan should be completed"
        );
    }

    public static void assertQualityScore(
            RoomScan roomScan,
            double minimumScore
    ) {
        Assert.assertTrue(
                roomScan.getQualityScore() >= minimumScore,
                "Room scan quality score below expected threshold"
        );
    }

    public static void assertRoomCount(
            RoomScan roomScan,
            int expected
    ) {
        Assert.assertEquals(
                roomScan.getRooms().size(),
                expected,
                "Unexpected number of rooms"
        );
    }

    public static void assertScanIdPresent(
            RoomScan roomScan
    ) {
        Assert.assertNotNull(
                roomScan.getScanId(),
                "Scan ID should not be null"
        );

        Assert.assertFalse(
                roomScan.getScanId().isBlank(),
                "Scan ID should not be blank"
        );
    }
}
