package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AerPfcEmissionsInitServiceTest {

    @InjectMocks
    private AerPfcEmissionsInitService service;

    @Test
    void initialize() {
        String sourceStreamId = "SS0001";
        SourceStream permitSourceStream =
            SourceStream.builder().id(sourceStreamId).type(SourceStreamType.COMBUSTION_COMMERCIAL_STANDARD_FUELS).build();
        PFCSourceStreamCategoryAppliedTier pfcSourceStreamCategoryAppliedTier =
            PFCSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                    .sourceStream(sourceStreamId)
                    .emissionSources(Set.of("ES0001"))
                    .categoryType(CategoryType.MAJOR)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                    .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                    .emissionPoints(Set.of("EP0001"))
                    .build())
                .activityData(PFCActivityData.builder()
                    .tier(ActivityDataTier.TIER_3)
                    .massBalanceApproachUsed(true)
                    .build())
                .emissionFactor(PFCEmissionFactor.builder()
                    .tier(PFCEmissionFactorTier.TIER_1)
                    .build())
                .build();

        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(permitSourceStream))
                .build()
            )
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder()
                        .sourceStreamCategoryAppliedTiers(List.of(pfcSourceStreamCategoryAppliedTier))
                        .build())
                )
                .build()
            )
            .build();

        CalculationPfcParameterMonitoringTier expectedCalculationPfcParameterMonitoringTier =
            CalculationPfcParameterMonitoringTier.builder()
            .activityDataTier(pfcSourceStreamCategoryAppliedTier.getActivityData().getTier())
            .emissionFactorTier(pfcSourceStreamCategoryAppliedTier.getEmissionFactor().getTier())
            .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);

        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.CALCULATION_PFC, aerMonitoringApproachEmissions.getType());
        assertThat(aerMonitoringApproachEmissions).isInstanceOf(CalculationOfPfcEmissions.class);

        CalculationOfPfcEmissions calculationOfPfcEmissions =
            (CalculationOfPfcEmissions) aerMonitoringApproachEmissions;
        List<PfcSourceStreamEmission> pfcSourceStreamEmissions =
            calculationOfPfcEmissions.getSourceStreamEmissions();
        assertThat(pfcSourceStreamEmissions).hasSize(1);

        PfcSourceStreamEmission pfcSourceStreamEmission = pfcSourceStreamEmissions.get(0);

        assertEquals(sourceStreamId, pfcSourceStreamEmission.getSourceStream());
        assertNotNull(pfcSourceStreamEmission.getId());
        assertEquals(pfcSourceStreamEmission.getEmissionSources(),
            pfcSourceStreamCategoryAppliedTier.getSourceStreamCategory().getEmissionSources());
        assertEquals(expectedCalculationPfcParameterMonitoringTier,
            pfcSourceStreamEmission.getParameterMonitoringTier());
        assertEquals(pfcSourceStreamEmission.getPfcSourceStreamEmissionCalculationMethodData().getCalculationMethod(),
            pfcSourceStreamCategoryAppliedTier.getSourceStreamCategory().getCalculationMethod());
        assertEquals(pfcSourceStreamEmission.getDurationRange(), DurationRange.builder().fullYearCovered(true).build());
    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.CALCULATION_PFC, service.getMonitoringApproachType());
    }
}
