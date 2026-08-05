package platform.core.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe local event bus used by the reference kernel and unit tests. */
public final class InMemoryPlatformEventBus implements PlatformEventPublisher {

    private final List<PlatformEventSubscriber> subscribers = new CopyOnWriteArrayList<>();
    private final List<PlatformEvent> history = new CopyOnWriteArrayList<>();

    public void subscribe(PlatformEventSubscriber subscriber) {
        subscribers.add(Objects.requireNonNull(subscriber, "subscriber"));
    }

    @Override
    public void publish(PlatformEvent event) {
        PlatformEvent safeEvent = Objects.requireNonNull(event, "event");
        history.add(safeEvent);
        for (PlatformEventSubscriber subscriber : subscribers) {
            subscriber.onEvent(safeEvent);
        }
    }

    public List<PlatformEvent> history() {
        return List.copyOf(history);
    }
}
