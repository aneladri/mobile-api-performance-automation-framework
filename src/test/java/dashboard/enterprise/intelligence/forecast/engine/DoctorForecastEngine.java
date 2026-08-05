package dashboard.enterprise.intelligence.forecast.engine;

import dashboard.enterprise.intelligence.forecast.model.ForecastSnapshot;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

public interface DoctorForecastEngine {
    ForecastSnapshot forecast(TrendSnapshot trend);
}
