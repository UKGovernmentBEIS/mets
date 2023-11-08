package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AerMonitoringApproachEmissionsInitializationService implements AerSectionInitializationService {
    private final List<AerMonitoringApproachTypeEmissionsInitService> aerMonitoringApproachTypeEmissionsInitServices;

    @Override
    public void initialize(Aer aer, Permit permit) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> permitMonitoringApproaches =
                permit.getMonitoringApproaches().getMonitoringApproaches();

        Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions = new EnumMap<>(MonitoringApproachType.class);

        permitMonitoringApproaches.keySet().forEach(type -> {
            AerMonitoringApproachEmissions aerMonitoringApproachEmissions = getMonitoringApproachEmissions(type, permit);
            if (aerMonitoringApproachEmissions != null) {
                monitoringApproachEmissions.put(type, aerMonitoringApproachEmissions);
            }
        });

        aer.setMonitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(monitoringApproachEmissions)
                .build()
        );
    }

    private AerMonitoringApproachEmissions getMonitoringApproachEmissions(MonitoringApproachType monitoringApproachType,
                                                                          Permit permit) {
        Optional<AerMonitoringApproachTypeEmissionsInitService> initService =
                aerMonitoringApproachTypeEmissionsInitServices.stream()
                        .filter(service -> monitoringApproachType.equals(service.getMonitoringApproachType()))
                        .findFirst();

        return initService.map(service -> service.initialize(permit))
                .orElse(null);
    }
}
