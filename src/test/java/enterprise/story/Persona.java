package enterprise.story;

public enum Persona {
    FIELD_TECHNICIAN("Field Technician"),
    PROPERTY_OWNER("Property Owner"),
    QUALITY_ENGINEER("Quality Engineer"),
    RELEASE_MANAGER("Release Manager"),
    ROOMSCAN_MOBILE_APP("RoomScan Mobile App"),
    ROOMSCAN_BACKEND("RoomScan Backend"),
    AI_GATEWAY("AI Gateway"),
    CUBICASA("CubiCasa"),
    ROOMSCAN_PORTAL("RoomScan Portal"),
    EXECUTIVE_DASHBOARD("Executive Dashboard");

    private final String displayName;

    Persona(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
