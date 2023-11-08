package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AerMeasurementCO2EmissionsInitServiceTest {

    @InjectMocks
    private AerMeasurementCO2EmissionsInitService service;

    @Test
    void initialize() {
        String emissionPointId = "EP0001";
        SourceStream permitSourceStream =
            SourceStream.builder().id(emissionPointId).type(SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS).build();
        TransferCO2 transferCO2 = TransferCO2.builder()
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .installationEmitter(InstallationEmitter.builder()
                .emitterId("emitterId")
                .email("test@test.com")
                .build())
            .transferType(TransferType.TRANSFER_CO2)
            .entryAccountingForTransfer(Boolean.TRUE)
            .build();
        MeasurementOfCO2EmissionPointCategoryAppliedTier measEmissionPointCategoryAppliedTier =
            MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                    .sourceStreams(Set.of("SS0001"))
                    .emissionSources(Set.of("ES0001"))
                    .categoryType(CategoryType.MAJOR)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                    .emissionPoint(emissionPointId)
                    .transfer(transferCO2)
                    .build()
                )
                .measuredEmissions(MeasurementOfCO2MeasuredEmissions.builder().tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_1).build())
                .build();

        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(permitSourceStream))
                .build()
            )
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder()
                        .hasTransfer(true)
                        .emissionPointCategoryAppliedTiers(List.of(measEmissionPointCategoryAppliedTier))
                        .build())
                )
                .build()
            )
            .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);

        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.MEASUREMENT_CO2, aerMonitoringApproachEmissions.getType());
        assertThat(aerMonitoringApproachEmissions).isInstanceOf(MeasurementOfCO2Emissions.class);

        MeasurementOfCO2Emissions measurementEmissions = (MeasurementOfCO2Emissions) aerMonitoringApproachEmissions;
        List<MeasurementCO2EmissionPointEmission> measurementEmissionPointEmissions =
            measurementEmissions.getEmissionPointEmissions();
        assertThat(measurementEmissionPointEmissions).hasSize(1);

        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission = measurementEmissionPointEmissions.get(0);

        assertEquals(emissionPointId, measurementEmissionPointEmission.getEmissionPoint());
        assertNotNull(measurementEmissionPointEmission.getId());
        assertEquals(measEmissionPointCategoryAppliedTier.getEmissionPointCategory().getEmissionSources(),
            measurementEmissionPointEmission.getEmissionSources());
        assertEquals(measEmissionPointCategoryAppliedTier.getEmissionPointCategory().getSourceStreams(),
            measurementEmissionPointEmission.getSourceStreams());
        assertEquals(measurementEmissionPointEmission.getTier(),
            measEmissionPointCategoryAppliedTier.getMeasuredEmissions().getTier());
        assertEquals(measurementEmissionPointEmission.getTransfer(),
            measEmissionPointCategoryAppliedTier.getEmissionPointCategory().getTransfer());
        assertThat(measurementEmissionPointEmission.getDurationRange().getFullYearCovered()).isTrue();
    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.MEASUREMENT_CO2, service.getMonitoringApproachType());
    }
}
