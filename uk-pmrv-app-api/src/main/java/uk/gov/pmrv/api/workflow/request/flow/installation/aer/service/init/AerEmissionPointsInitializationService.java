package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.Aer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerEmissionPointsInitializationService implements AerSectionInitializationService{

    public void initialize(Aer aer, Permit permit) {
        Map<MonitoringApproachType, PermitMonitoringApproachSection> permitMonitoringApproaches =
            permit.getMonitoringApproaches().getMonitoringApproaches();
        Set<MonitoringApproachType> permitMonitoringApproachTypes = new HashSet<>(permitMonitoringApproaches.keySet());

        permitMonitoringApproachTypes.retainAll(Set.of(MonitoringApproachType.MEASUREMENT_CO2, MonitoringApproachType.MEASUREMENT_N2O));
        if(permitMonitoringApproachTypes.isEmpty()) {
            return;
        }

        List<EmissionPoint> permitEmissionPoints = permit.getEmissionPoints().getEmissionPoints();
        Set<String> referencedEmissionPointIds = getEmissionPointsReferencedInMeasAndN2OApproaches(permitMonitoringApproaches);

        List<EmissionPoint> aerEmissionPoints = permitEmissionPoints.stream()
            .filter(emissionPoint -> referencedEmissionPointIds.stream().anyMatch(id -> id.equals(emissionPoint.getId())))
            .collect(Collectors.toList());

        aer.setEmissionPoints(EmissionPoints.builder().emissionPoints(aerEmissionPoints).build());
}

    private Set<String> getEmissionPointsReferencedInMeasAndN2OApproaches(Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches) {
        Set<String> emissionPointIds = new HashSet<>();

        MeasurementOfCO2MonitoringApproach measMonitoringApproach =
            (MeasurementOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT_CO2);
        if(measMonitoringApproach != null) {
            emissionPointIds.add(measMonitoringApproach.getEmissionPoint());
        }

        MeasurementOfN2OMonitoringApproach n2oMonitoringApproach =
            (MeasurementOfN2OMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT_N2O);
        if(n2oMonitoringApproach != null) {
            emissionPointIds.add(n2oMonitoringApproach.getEmissionPoint());
        }

        return emissionPointIds;
    }
}
