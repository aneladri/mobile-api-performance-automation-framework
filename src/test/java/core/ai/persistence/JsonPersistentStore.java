package core.ai.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class JsonPersistentStore<T> {

    protected static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .enable(
                            SerializationFeature.INDENT_OUTPUT
                    );

    protected abstract Path storePath();

    protected void ensureDirectoryExists() {

        try {

            Path parent =
                    storePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

        } catch (IOException exception) {

            throw new PersistentStoreException(
                    "Could not create persistence directory",
                    exception
            );
        }
    }

    protected void writeJson(
            Object value) {

        ensureDirectoryExists();

        Path target =
                storePath();

        Path temporary =
                target.resolveSibling(
                        target.getFileName()
                                + ".tmp"
                );

        try {

            OBJECT_MAPPER.writeValue(
                    temporary.toFile(),
                    value
            );

            moveAtomically(
                    temporary,
                    target
            );

        } catch (IOException exception) {

            throw new PersistentStoreException(
                    "Could not persist JSON store",
                    exception
            );
        }
    }

    protected <R> R readJson(
            Class<R> type) {

        Path target =
                storePath();

        if (!Files.exists(target)) {
            return null;
        }

        try {

            return OBJECT_MAPPER.readValue(
                    target.toFile(),
                    type
            );

        } catch (IOException exception) {

            throw new PersistentStoreException(
                    "Could not read JSON store",
                    exception
            );
        }
    }

    protected void deleteStore() {

        try {

            Files.deleteIfExists(
                    storePath()
            );

        } catch (IOException exception) {

            throw new PersistentStoreException(
                    "Could not delete JSON store",
                    exception
            );
        }
    }

    protected static void moveAtomically(
            Path source,
            Path target)
            throws IOException {

        try {

            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );

        } catch (AtomicMoveNotSupportedException exception) {

            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}