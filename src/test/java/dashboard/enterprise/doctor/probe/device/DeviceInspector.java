package dashboard.enterprise.doctor.probe.device;

import dashboard.enterprise.doctor.probe.DoctorContext;

public interface DeviceInspector {

    DeviceInventoryResult inspect(DoctorContext context);
}
