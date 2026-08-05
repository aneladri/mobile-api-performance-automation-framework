package dashboard.enterprise.intelligence.trend.engine;

import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

public interface DoctorTrendEngine {
    TrendSnapshot analyze(DoctorHistoryIndex index);
}
