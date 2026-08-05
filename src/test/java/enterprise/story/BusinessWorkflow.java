package enterprise.story;

import java.util.List;

public record BusinessWorkflow(
        String name,
        String objective,
        List<BusinessTransaction> transactions
) {
    public BusinessWorkflow {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workflow name must not be blank");
        }
        if (objective == null || objective.isBlank()) {
            throw new IllegalArgumentException("Workflow objective must not be blank");
        }
        transactions = List.copyOf(transactions == null ? List.of() : transactions);
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("Workflow must contain at least one transaction");
        }
    }
}
