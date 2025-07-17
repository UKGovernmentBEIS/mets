package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AerMeasurementN2OEmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        final MeasurementOfN2OMonitoringApproach measurementMonitoringApproach =
            (MeasurementOfN2OMonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());

        final List<MeasurementOfN2OEmissionPointCategoryAppliedTier> emissionPointCategoryAppliedTiers =
            measurementMonitoringApproach.getEmissionPointCategoryAppliedTiers();

        List<MeasurementN2OEmissionPointEmission> measurementEmissionPointEmissions = new ArrayList<>();

        emissionPointCategoryAppliedTiers.forEach(emissionPointCategoryAppliedTier -> {
                MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
                    buildMeasurementSourceStreamEmission(emissionPointCategoryAppliedTier);
                measurementEmissionPointEmissions.add(measurementEmissionPointEmission);
            }
        );

        return MeasurementOfN2OEmissions.builder().type(getMonitoringApproachType())
            .hasTransfer(measurementMonitoringApproach.isHasTransfer())
            .emissionPointEmissions(measurementEmissionPointEmissions)
            .build();
    }

    private MeasurementN2OEmissionPointEmission buildMeasurementSourceStreamEmission(MeasurementOfN2OEmissionPointCategoryAppliedTier emissionPointCategoryAppliedTier) {

        MeasurementOfN2OEmissionPointCategory emissionPointCategory =
            emissionPointCategoryAppliedTier.getEmissionPointCategory();
        String emissionPointId = emissionPointCategory.getEmissionPoint();
        TransferN2O transfer = emissionPointCategoryAppliedTier.getEmissionPointCategory().getTransfer();

        return MeasurementN2OEmissionPointEmission.builder()
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
        return MonitoringApproachType.MEASUREMENT_N2O;
    }
}
