package performance.postman;

import java.util.List;

public class PostmanCollection {

    private final String name;
    private final List<PostmanFolder> folders;

    public PostmanCollection(
            String name,
            List<PostmanFolder> folders
    ) {
        this.name = name;
        this.folders = folders;
    }

    public String getName() {
        return name;
    }

    public List<PostmanFolder> getFolders() {
        return folders;
    }

    public int getRequestCount() {

        return folders.stream()
                .mapToInt(folder ->
                        folder.getRequests().size())
                .sum();
    }
}
