package performance.postman;

public class PostmanRequest {

    private final String name;
    private final String method;
    private final String endpoint;

    public PostmanRequest(
            String name,
            String method,
            String endpoint
    ) {
        this.name = name;
        this.method = method;
        this.endpoint = endpoint;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
