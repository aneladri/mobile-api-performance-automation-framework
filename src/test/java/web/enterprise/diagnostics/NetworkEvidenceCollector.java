package web.enterprise.diagnostics;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NetworkEvidenceCollector {

    private final List<NetworkEvent> events = new ArrayList<>();

    public void register(Page page) {
        page.onResponse(this::capture);
    }

    private void capture(Response response) {
        if (response.url().contains("roomscan.local/api/")) {
            events.add(new NetworkEvent(
                    response.request().method(),
                    response.url(),
                    response.status(),
                    response.statusText()
            ));
        }
    }

    public List<NetworkEvent> events() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public long failedCount() {
        return events.stream().filter(event -> event.status() >= 400).count();
    }

    public record NetworkEvent(
            String method,
            String url,
            int status,
            String statusText
    ) {
    }
}
