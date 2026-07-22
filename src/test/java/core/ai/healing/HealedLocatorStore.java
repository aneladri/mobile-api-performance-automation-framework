package core.ai.healing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HealedLocatorStore {

    private static final Map<String, HealedLocatorCandidate> CACHE =
            new ConcurrentHashMap<>();

    private HealedLocatorStore() {
    }

    public static void store(
            String brokenLocator,
            HealedLocatorCandidate candidate,
            String screen,
            String test
    ) {
        CACHE.put(
                brokenLocator,
                candidate
        );
    }

    public static HealedLocatorCandidate getCached(
            String brokenLocator
    ) {
        return CACHE.get(
                brokenLocator
        );
    }

    public static boolean has(
            String brokenLocator
    ) {
        return CACHE.containsKey(
                brokenLocator
        );
    }

    public static void clear() {
        CACHE.clear();
    }
}
