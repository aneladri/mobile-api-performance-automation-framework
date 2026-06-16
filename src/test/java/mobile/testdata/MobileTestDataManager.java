package mobile.testdata;

import mobile.models.User;

public final class MobileTestDataManager {

    private MobileTestDataManager() {
    }

    public static User getAdminUser() {
        return new User(
                "admin@test.com",
                "Password123"
        );
    }

    public static User getStandardUser() {
        return new User(
                "user@test.com",
                "Password123"
        );
    }
}
