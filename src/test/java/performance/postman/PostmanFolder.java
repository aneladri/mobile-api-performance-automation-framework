package performance.postman;

import java.util.List;

public class PostmanFolder {

    private final String name;
    private final List<PostmanRequest> requests;

    public PostmanFolder(
            String name,
            List<PostmanRequest> requests
    ) {
        this.name = name;
        this.requests = requests;
    }

    public String getName() {
        return name;
    }

    public List<PostmanRequest> getRequests() {
        return requests;
    }
}
