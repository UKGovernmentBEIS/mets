package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AerMeasurementCO2EmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        final MeasurementOfCO2MonitoringApproach measurementMonitoringApproach =
            (MeasurementOfCO2MonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());

        final List<MeasurementOfCO2EmissionPointCategoryAppliedTier> emissionPointCategoryAppliedTiers =
            measurementMonitoringApproach.getEmissionPointCategoryAppliedTiers();

        List<MeasurementCO2EmissionPointEmission> measurementEmissionPointEmissions = new ArrayList<>();

        emissionPointCategoryAppliedTiers.forEach(emissionPointCategoryAppliedTier -> {
                    MeasurementCO2EmissionPointEmission measurementEmissionPointEmission =
                            buildMeasurementSourceStreamEmission(emissionPointCategoryAppliedTier);
                    measurementEmissionPointEmissions.add(measurementEmissionPointEmission);
                }
        );

        return MeasurementOfCO2Emissions.builder().type(getMonitoringApproachType())
            .hasTransfer(measurementMonitoringApproach.isHasTransfer())
            .emissionPointEmissions(measurementEmissionPointEmissions)
            .build();
    }

    private MeasurementCO2EmissionPointEmission buildMeasurementSourceStreamEmission(MeasurementOfCO2EmissionPointCategoryAppliedTier emissionPointCategoryAppliedTier) {

        MeasurementOfCO2EmissionPointCategory emissionPointCategory =
            emissionPointCategoryAppliedTier.getEmissionPointCategory();
        String emissionPointId = emissionPointCategory.getEmissionPoint();
        TransferCO2 transfer = emissionPointCategoryAppliedTier.getEmissionPointCategory().getTransfer();

        return MeasurementCO2EmissionPointEmission.builder()
            .id(UUID.randomUUID().toString())
            .emissionPoint(emissionPointId)
            .emissionSources(emissionPointCategory.getEmissionSources())
            .sourceStreams(emissionPointCategory.getSourceStreams())
            .tier(emissionPointCategoryAppliedTier.getMeasuredEmissions().getTier())
            .durationRange(DurationRange.builder().fullYearCovered(true).build())
            .transfer(transfer)
            .build();

    }

    @Override
    public MonitoringApproachType getMonitoringApproachType() {
        return MonitoringApproachType.MEASUREMENT_CO2;
    }
}
