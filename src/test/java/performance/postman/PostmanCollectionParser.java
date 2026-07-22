package performance.postman;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PostmanCollectionParser {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public PostmanCollection parse(
            Path collectionFile
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(
                            collectionFile.toFile()
                    );

            String collectionName =
                    root.path("info")
                            .path("name")
                            .asText("Unnamed Collection");

            List<PostmanFolder> folders =
                    new ArrayList<>();

            JsonNode items =
                    root.path("item");

            if (items.isArray()) {

                for (JsonNode folderNode : items) {

                    String folderName =
                            folderNode.path("name").asText();

                    List<PostmanRequest> requests =
                            new ArrayList<>();

                    JsonNode requestItems =
                            folderNode.path("item");

                    if (requestItems.isArray()) {

                        for (JsonNode requestNode : requestItems) {

                            JsonNode request =
                                    requestNode.path("request");

                            requests.add(
                                    new PostmanRequest(
                                            requestNode.path("name").asText(),
                                            request.path("method").asText(),
                                            request.path("url").path("raw").asText()
                                    )
                            );
                        }
                    }

                    folders.add(
                            new PostmanFolder(
                                    folderName,
                                    requests
                            )
                    );
                }
            }

            return new PostmanCollection(
                    collectionName,
                    folders
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to parse Postman collection.",
                    e
            );
        }
    }
}
