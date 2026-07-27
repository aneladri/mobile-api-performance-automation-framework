package mobile.testdata;

import core.utils.JsonUtils;
import io.restassured.path.json.JsonPath;
import mobile.models.User;

/**
 * Loads real mobile test users from src/test/resources/testdata/mobile/login-users.json.
 *
 * Previously this class hardcoded the same two users directly in Java, while an
 * identical, unused login-users.json sat right next to it - neither was ever
 * actually referenced by any test. Consolidated tonight into one real mechanism:
 * this class is now the only place user data is defined (the JSON file), and the
 * only accessor (this class) - matching the pattern already used for API test
 * data via PayloadManager + JsonUtils.
 */
public final class MobileTestDataManager {

    private static final String LOGIN_USERS_PATH =
            "src/test/resources/testdata/mobile/login-users.json";

    private MobileTestDataManager() {
    }

    public static User getAdminUser() {
        return loadUser("admin");
    }

    public static User getStandardUser() {
        return loadUser("standardUser");
    }

    private static User loadUser(String key) {

        JsonPath json =
                new JsonPath(JsonUtils.getJsonAsString(LOGIN_USERS_PATH));

        String username = json.getString(key + ".username");
        String password = json.getString(key + ".password");

        if (username == null || password == null) {
            throw new IllegalStateException(
                    "No test user found in login-users.json for key: " + key
            );
        }

        return new User(username, password);
    }
}
