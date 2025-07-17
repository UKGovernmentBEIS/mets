package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PermitOriginatedCalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

@ExtendWith(MockitoExtension.class)
class AerPermitOriginatedDataBuilderServiceTest {

    @Mock
    private AerRequestQueryService aerRequestQueryService;

    @InjectMocks
    private AerPermitOriginatedDataBuilderService aerPermitOriginatedDataBuilderService;

    @Test
    void buildPermitOriginatedData() {
        final long accountId = 1L;
        final MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(
                Map.of(
                    MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .sourceStreamEmissions(List.of(CalculationSourceStreamEmission.builder()
                            .id("SS-UUID-1")
                            .sourceStream("SS0001")
                            .emissionSources(Set.of("ES0001"))
                            .parameterMonitoringTiers(List.of(
                                    CalculationActivityDataMonitoringTier.builder()
                                        .type(CalculationParameterType.ACTIVITY_DATA)
                                        .tier(CalculationActivityDataTier.TIER_3)
                                        .build(),
                                    CalculationNetCalorificValueMonitoringTier.builder()
                                        .type(CalculationParameterType.NET_CALORIFIC_VALUE)
                                        .tier(CalculationNetCalorificValueTier.TIER_2B)
                                        .build()
                                )
                            )
                            .build())
                        )
                        .build(),
                    MonitoringApproachType.MEASUREMENT_CO2,
                    MeasurementOfCO2Emissions.builder()
                        .type(MonitoringApproachType.MEASUREMENT_CO2)
                        .emissionPointEmissions(List.of(
                            MeasurementCO2EmissionPointEmission.builder()
                                .id("EP-UUID-1")
                                .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
                                .build()
                        ))
                        .build(),
                    MonitoringApproachType.MEASUREMENT_N2O,
                    MeasurementOfN2OEmissions.builder()
                        .type(MonitoringApproachType.MEASUREMENT_N2O)
                        .emissionPointEmissions(List.of(
                            MeasurementN2OEmissionPointEmission.builder()
                                .id("EP-UUID-2")
                                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
                                .build()
                        ))
                        .build(),
                    MonitoringApproachType.CALCULATION_PFC,
                    CalculationOfPfcEmissions.builder()
                        .type(MonitoringApproachType.CALCULATION_PFC)
                        .sourceStreamEmissions(List.of(
                            PfcSourceStreamEmission.builder()
                                .id("SS-UUID-2")
                                .parameterMonitoringTier(CalculationPfcParameterMonitoringTier.builder().build())
                                .build()
                        ))
                        .build()
                )
            )
            .build();
        final Aer aer = Aer.builder()
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .build();
        final InstallationCategory installationCategory = InstallationCategory.A;
        final List<String> permitNotificationIds = List.of("AEMN1-1", "AEMN1-4");

        PermitOriginatedData expected = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                    List.of(
                        CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA)
                            .tier(
                                CalculationActivityDataTier.TIER_3).build(),
                        CalculationNetCalorificValueMonitoringTier.builder()
                            .type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(
                                CalculationNetCalorificValueTier.TIER_2B).build()
                    )
                ))
                .measurementCO2EmissionPointParamMonitoringTiers(Map.of("EP-UUID-1",
                    MeasurementOfCO2MeasuredEmissionsTier.TIER_2
                ))
                .measurementN2OEmissionPointParamMonitoringTiers(Map.of("EP-UUID-2",
                    MeasurementOfN2OMeasuredEmissionsTier.TIER_2
                ))
                .calculationPfcSourceStreamParamMonitoringTiers(Map.of("SS-UUID-2",
                    PermitOriginatedCalculationPfcParameterMonitoringTier.builder().build()
                ))
                .build())
            .permitType(permitContainer.getPermitType())
            .permitNotificationIds(permitNotificationIds)
            .installationCategory(installationCategory)
            .build();

        when(aerRequestQueryService.getApprovedPermitNotificationIdsByAccount(accountId)).thenReturn(permitNotificationIds);

        // Invoke
        PermitOriginatedData actual =
            aerPermitOriginatedDataBuilderService.buildPermitOriginatedData(accountId, aer, permitContainer,
                installationCategory);

        // Verify
        verify(aerRequestQueryService, times(1)).getApprovedPermitNotificationIdsByAccount(accountId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}