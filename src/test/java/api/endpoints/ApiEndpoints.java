package api.endpoints;

public final class ApiEndpoints {

    private ApiEndpoints() {
    }

    public static final String STATUS_200 = "/status/200";
    public static final String STATUS_CODE = "/status/{code}";
    public static final String POST = "/post";
    public static final String HEADERS = "/headers";
    public static final String BASIC_AUTH = "/basic-auth/{username}/{password}";
}
