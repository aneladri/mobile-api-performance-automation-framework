package web.enterprise.flow;

import web.enterprise.pages.RoomScanPortalPage;

public final class RoomScanPortalFlow {

    private final RoomScanPortalPage portal;

    public RoomScanPortalFlow(RoomScanPortalPage portal) {
        this.portal = portal;
    }

    public void openAndAuthenticate() {
        portal.openDemoPortal();
        portal.login();
    }

    public void locateCompletedScan(String scanId) {
        portal.searchScan(scanId);
    }

    public void reviewAiFloorPlan() {
        portal.reviewFloorPlan();
    }

    public void approveAndSubmit() {
        portal.approve();
        portal.submit();
    }
}
