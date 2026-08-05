package web.locators;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocatorRepository {

    private final Map<String, WebLocator> locators = new LinkedHashMap<>();

    public LocatorRepository register(
            WebLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException(
                    "Web locator must not be null");
        }

        String key = normalize(locator.getName());

        if (locators.containsKey(key)) {
            WebLocator existingLocator = locators.get(key);

            throw new IllegalArgumentException(
                    "Locator already registered: "
                            + existingLocator.getName());
        }

        locators.put(key, locator);

        return this;
    }

    public WebLocator get(
            String locatorName) {
        String key = normalize(locatorName);

        WebLocator locator = locators.get(key);

        if (locator == null) {
            throw new IllegalArgumentException(
                    "Locator not found: "
                            + locatorName);
        }

        return locator;
    }

    public boolean contains(
            String locatorName) {
        return locators.containsKey(
                normalize(locatorName));
    }

    public int size() {
        return locators.size();
    }

    public Map<String, WebLocator> getAll() {
        return new LinkedHashMap<>(locators);
    }

    private String normalize(
            String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Locator name must not be blank");
        }

        return value.trim().toLowerCase();
    }
}
