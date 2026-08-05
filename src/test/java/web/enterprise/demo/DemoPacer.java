package web.enterprise.demo;

public final class DemoPacer {
    private static final long DEFAULT_DELAY_MILLIS = 1_500L;

    private DemoPacer() {
    }

    public static void pause(String moment) {
        if (!Boolean.parseBoolean(System.getProperty("mapaf.demo.pacing.enabled", "true"))) {
            return;
        }
        long delay = delayMillis();
        System.out.printf("[Demo] %s - pausing %.1f sec%n", moment, delay / 1000.0);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Demo pacing was interrupted", exception);
        }
    }

    private static long delayMillis() {
        try {
            return Math.max(0L, Long.parseLong(System.getProperty("mapaf.demo.pacing.millis", String.valueOf(DEFAULT_DELAY_MILLIS))));
        } catch (NumberFormatException exception) {
            return DEFAULT_DELAY_MILLIS;
        }
    }
}
