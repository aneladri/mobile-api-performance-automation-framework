package roomscan.execution;

import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

public class ExecutionEngine {

    private static final Logger logger =
            LoggerUtil.getLogger(ExecutionEngine.class);

    public void execute(
            ExecutionPlan plan
    ) {
        for (ExecutionStep step : plan.getSteps()) {
            logger.info(
                    "[RoomScan Execution] {} - {}",
                    step.getName(),
                    step.getDescription()
            );
        }
    }
}
