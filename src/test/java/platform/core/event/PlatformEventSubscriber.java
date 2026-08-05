package platform.core.event;

@FunctionalInterface
public interface PlatformEventSubscriber {

    void onEvent(PlatformEvent event);
}
