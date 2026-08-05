package enterprise.story;

public final class RoomScanStoryCli {

    private RoomScanStoryCli() {
    }

    public static void main(String[] args) {
        BusinessScenario scenario = RoomScanStory.endToEndScenario();

        System.out.println("======================================================================");
        System.out.println("        MAPAF ENTERPRISE QUALITY PLATFORM - ROOMSCAN STORY");
        System.out.println("======================================================================");
        field("Scenario", scenario.name());
        field("Business Objective", scenario.businessObjective());
        field("Personas", scenario.personas().size());
        field("Workflow Stages", scenario.workflow().transactions().size());

        System.out.println();
        System.out.println("Business Workflow");
        System.out.println("----------------------------------------------------------------------");
        scenario.workflow().transactions().forEach(transaction ->
                System.out.printf("%-7s %-30s Actor: %s%n",
                        transaction.id(), transaction.name(), transaction.actor().displayName()));

        System.out.println();
        System.out.println("Production Workload Model");
        System.out.println("----------------------------------------------------------------------");
        RoomScanStory.productionMetrics().forEach(metric ->
                System.out.printf("%-28s : %.0f %s%n", metric.name(), metric.value(), metric.unit()));

        ExecutiveOutcome outcome = RoomScanStory.readyOutcome();
        System.out.println();
        System.out.println("Executive Outcome");
        System.out.println("----------------------------------------------------------------------");
        field("Readiness Score", outcome.readinessScore() + "%");
        field("Risk", outcome.riskLevel());
        field("Recommendation", outcome.recommendation());
        System.out.println("======================================================================");
    }

    private static void field(String name, Object value) {
        System.out.printf("%-28s : %s%n", name, value);
    }
}
