package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AerMeasurementN2OEmissionsInitServiceTest {

    @InjectMocks
    private AerMeasurementN2OEmissionsInitService service;

    @Test
    void initialize() {
        String emissionPointId = "EP0001";
        SourceStream permitSourceStream = SourceStream.builder().id(emissionPointId).type(SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS).build();
        MeasurementOfN2OEmissionPointCategoryAppliedTier measEmissionPointCategoryAppliedTier =
                MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                        .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                                .sourceStreams(Set.of("SS0001"))
                                .emissionSources(Set.of("ES0001"))
                                .categoryType(CategoryType.MAJOR)
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                                .emissionPoint(emissionPointId)
                                .build()
                        )
                        .measuredEmissions(MeasurementOfN2OMeasuredEmissions.builder().tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_1).build())
                        .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder()
                        .sourceStreams(List.of(permitSourceStream))
                        .build()
                )
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(
                                Map.of(MonitoringApproachType.MEASUREMENT_N2O, MeasurementOfN2OMonitoringApproach.builder()
                                        .hasTransfer(true)
                                        .emissionPointCategoryAppliedTiers(List.of(measEmissionPointCategoryAppliedTier))
                                        .build())
                        )
                        .build()
                )
                .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);

        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.MEASUREMENT_N2O, aerMonitoringApproachEmissions.getType());
        assertThat(aerMonitoringApproachEmissions).isInstanceOf(MeasurementOfN2OEmissions.class);

        MeasurementOfN2OEmissions measurementEmissions = (MeasurementOfN2OEmissions) aerMonitoringApproachEmissions;
        List<MeasurementN2OEmissionPointEmission> measurementEmissionPointEmissions =
                measurementEmissions.getEmissionPointEmissions();
        assertThat(measurementEmissionPointEmissions).hasSize(1);

        MeasurementEmissionPointEmission measurementEmissionPointEmission = measurementEmissionPointEmissions.get(0);

        assertEquals(emissionPointId, measurementEmissionPointEmission.getEmissionPoint());
        assertNotNull(measurementEmissionPointEmission.getId());
        assertEquals(measEmissionPointCategoryAppliedTier.getEmissionPointCategory().getEmissionSources(),
                measurementEmissionPointEmission.getEmissionSources());
        assertEquals(measEmissionPointCategoryAppliedTier.getEmissionPointCategory().getSourceStreams(),
                measurementEmissionPointEmission.getSourceStreams());
    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.MEASUREMENT_N2O, service.getMonitoringApproachType());
    }
}
