package web.enterprise.pages;

import com.microsoft.playwright.Page;
import web.pages.BasePage;

public final class RoomScanPortalPage extends BasePage {

    private static final String LOGIN_BUTTON = "[data-testid='login-button']";
    private static final String DASHBOARD = "[data-testid='dashboard']";
    private static final String SEARCH_INPUT = "[data-testid='scan-search']";
    private static final String SEARCH_BUTTON = "[data-testid='search-button']";
    private static final String SCAN_RESULT = "[data-testid='scan-result']";
    private static final String REVIEW_BUTTON = "[data-testid='review-button']";
    private static final String FLOOR_PLAN = "[data-testid='floor-plan']";
    private static final String METADATA = "[data-testid='metadata']";
    private static final String APPROVE_BUTTON = "[data-testid='approve-button']";
    private static final String SUBMIT_BUTTON = "[data-testid='submit-button']";
    private static final String STATUS = "[data-testid='status']";

    public RoomScanPortalPage(Page page) {
        super(page);
    }

    public void openDemoPortal() {
        page().setContent(PORTAL_HTML);
    }

    public void login() {
        click(LOGIN_BUTTON);
        waitForVisible(DASHBOARD);
    }

    public boolean isDashboardVisible() {
        return isVisible(DASHBOARD);
    }

    public void searchScan(String scanId) {
        fill(SEARCH_INPUT, scanId);
        click(SEARCH_BUTTON);
        waitForVisible(SCAN_RESULT);
    }

    public String getSearchResult() {
        return innerText(SCAN_RESULT);
    }

    public void reviewFloorPlan() {
        click(REVIEW_BUTTON);
        waitForVisible(FLOOR_PLAN);
    }

    public boolean isFloorPlanVisible() {
        return isVisible(FLOOR_PLAN);
    }

    public String getMetadata() {
        return innerText(METADATA);
    }

    public void approve() {
        click(APPROVE_BUTTON);
    }

    public void submit() {
        click(SUBMIT_BUTTON);
    }

    public String getStatus() {
        return innerText(STATUS);
    }

    private static final String PORTAL_HTML = """
            <!doctype html>
            <html lang='en'>
            <head>
              <meta charset='utf-8'>
              <title>RoomScan Quality Portal</title>
              <style>
                body { font-family: Arial, sans-serif; margin: 32px; background:#f4f6f8; }
                .card { background:white; border-radius:12px; padding:24px; max-width:820px; box-shadow:0 4px 16px rgba(0,0,0,.08); }
                button { margin:8px 8px 8px 0; padding:10px 16px; }
                input { padding:10px; width:240px; }
                .hidden { display:none; }
                .ok { color:#18794e; font-weight:bold; }
              </style>
            </head>
            <body>
              <main class='card'>
                <h1>RoomScan Operations Portal</h1>
                <button data-testid='login-button' onclick='login()'>Sign in as Quality Engineer</button>
                <section data-testid='dashboard' class='hidden'>
                  <h2>Inspection Dashboard</h2>
                  <input data-testid='scan-search' aria-label='Scan ID' placeholder='RS-1045'>
                  <button data-testid='search-button' onclick='searchScan()'>Search</button>
                  <div data-testid='scan-result' class='hidden'></div>
                  <button data-testid='review-button' class='hidden' onclick='reviewPlan()'>Review AI Floor Plan</button>
                  <section data-testid='floor-plan' class='hidden'>
                    <h3>AI Floor Plan</h3>
                    <p data-testid='metadata'>3 rooms | 12 walls | Quality HIGH | Provider CubiCasa</p>
                    <button data-testid='approve-button' onclick='approve()'>Approve</button>
                    <button data-testid='submit-button' onclick='submitScan()'>Submit Scan</button>
                  </section>
                  <p data-testid='status' class='ok'></p>
                </section>
              </main>
              <script>
                async function login() {
                  const r = await fetch('https://roomscan.local/api/auth/login', {method:'POST'});
                  if (r.ok) document.querySelector('[data-testid=dashboard]').classList.remove('hidden');
                }
                async function searchScan() {
                  const id = document.querySelector('[data-testid=scan-search]').value || 'RS-1045';
                  const r = await fetch('https://roomscan.local/api/scans/' + id);
                  const data = await r.json();
                  const result = document.querySelector('[data-testid=scan-result]');
                  result.textContent = data.scanId + ' | AI processing complete | Floor plan ready';
                  result.classList.remove('hidden');
                  document.querySelector('[data-testid=review-button]').classList.remove('hidden');
                }
                async function reviewPlan() {
                  const r = await fetch('https://roomscan.local/api/scans/RS-1045/floorplan');
                  if (r.ok) document.querySelector('[data-testid=floor-plan]').classList.remove('hidden');
                }
                async function approve() {
                  const r = await fetch('https://roomscan.local/api/scans/RS-1045/approve', {method:'POST'});
                  if (r.ok) document.querySelector('[data-testid=status]').textContent = 'Floor plan approved';
                }
                async function submitScan() {
                  const r = await fetch('https://roomscan.local/api/scans/RS-1045/submit', {method:'POST'});
                  if (r.ok) document.querySelector('[data-testid=status]').textContent = 'Scan submitted and dashboard updated';
                }
              </script>
            </body>
            </html>
            """;
}
