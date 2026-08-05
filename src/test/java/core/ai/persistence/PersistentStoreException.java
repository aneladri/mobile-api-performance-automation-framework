package core.ai.persistence;

public class PersistentStoreException
        extends RuntimeException {

    public PersistentStoreException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}