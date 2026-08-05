package roomscan.models;

import java.time.Instant;
import java.util.List;

public class RoomScan {

    private final String scanId;
    private final Instant capturedAt;
    private final List<Room> rooms;
    private final String status;
    private final double qualityScore;

    public RoomScan(
            String scanId,
            Instant capturedAt,
            List<Room> rooms,
            String status,
            double qualityScore
    ) {
        this.scanId = scanId;
        this.capturedAt = capturedAt;
        this.rooms = rooms;
        this.status = status;
        this.qualityScore = qualityScore;
    }

    public String getScanId() {
        return scanId;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public String getStatus() {
        return status;
    }

    public double getQualityScore() {
        return qualityScore;
    }
}
