package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
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
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerEmissionPointsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerEmissionSourcesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerMonitoringApproachEmissionsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerRegulatedActivitiesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSectionInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSourceStreamsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplicationSubmitInitializerTest {

    @InjectMocks
    private AerApplicationSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private AerRequestQueryService aerRequestQueryService;

    @Spy
    private ArrayList<AerSectionInitializationService> aerSectionInitializationServices;

    @Mock
    private AerEmissionPointsInitializationService aerEmissionPointsInitService;

    @Mock
    private AerMonitoringApproachEmissionsInitializationService aerMonitoringApproachEmissionsInitializationService;

    @Mock
    private AerEmissionSourcesInitializationService aerEmissionSourcesInitService;

    @Mock
    private AerSourceStreamsInitializationService aerSourceStreamsInitService;

    @Mock
    private AerRegulatedActivitiesInitializationService aerRegulatedActivitiesInitializationService;

    @Mock
    private PermitVariationRequestQueryService permitVariationRequestQueryService;

    @BeforeEach
    void setUp() {
        aerSectionInitializationServices.add(aerEmissionPointsInitService);
        aerSectionInitializationServices.add(aerMonitoringApproachEmissionsInitializationService);
        aerSectionInitializationServices.add(aerEmissionSourcesInitService);
        aerSectionInitializationServices.add(aerSourceStreamsInitService);
        aerSectionInitializationServices.add(aerRegulatedActivitiesInitializationService);
    }

    @Test
    void initializePayload() {
        final long accountId = 1L;
        final LocalDate permitVariationEndDate = LocalDate.now().minusYears(1);
        final LocalDate permitIssuanceEndDate = LocalDate.now().minusYears(2);
        final Year reportingYear = Year.now().minusYears(1);
        final Request request = Request.builder()
            .accountId(1L)
            .payload(AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .build())
            .metadata(AerRequestMetadata.builder().year(reportingYear).build()).build();
        final MonitoringPlanVersion monitoringPlanVersionIssuance = MonitoringPlanVersion.builder()
            .permitId("permit")
            .permitConsolidationNumber(1)
            .endDate(permitIssuanceEndDate).build();
        final MonitoringPlanVersion monitoringPlanVersionVariation = MonitoringPlanVersion.builder()
            .permitId("permit")
            .permitConsolidationNumber(10)
            .endDate(permitVariationEndDate).build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("Account name")
            .siteName("Site name")
            .installationLocation(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("ST330000")
                .address(AddressDTO.builder()
                    .line1("line1")
                    .city("city")
                    .country("GB")
                    .postcode("postcode")
                    .build())
                .build())
            .operator("le")
            .operatorType(LegalEntityType.LIMITED_COMPANY)
            .companyReferenceNumber("408812")
            .operatorDetailsAddress(AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GR")
                .postcode("postcode")
                .build())
            .build();
        final SourceStreams sourceStreams = SourceStreams.builder()
            .sourceStreams(List.of(
                SourceStream.builder()
                    .type(SourceStreamType.COMBUSTION_FLARES)
                    .reference("reference 1")
                    .build(),
                SourceStream.builder()
                    .type(SourceStreamType.CEMENT_CLINKER_CKD)
                    .reference("reference 2")
                    .build()))
            .build();

        final MonitoringApproaches monitoringApproaches = MonitoringApproaches.builder()
            .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION_CO2,
                CalculationOfCO2MonitoringApproach.builder()
                    .sourceStreamCategoryAppliedTiers(List.of(
                        CalculationSourceStreamCategoryAppliedTier.builder()
                            .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                                .sourceStream("SS0001")
                                .emissionSources(Set.of("ES0001"))
                                .categoryType(CategoryType.MAJOR)
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                                .build())
                            .activityData(CalculationActivityData.builder().tier(CalculationActivityDataTier.TIER_3).build())
                            .netCalorificValue(CalculationNetCalorificValue.builder().tier(CalculationNetCalorificValueTier.TIER_2B).build())
                            .build())
                    )
                    .build())
            )
            .build();

        final EmissionSources emissionSources = EmissionSources.builder()
            .emissionSources(List.of(
                EmissionSource.builder()
                    .reference("reference 1")
                    .description("description 1")
                    .build(),
                EmissionSource.builder()
                    .reference("reference 2")
                    .description("description 2")
                    .build()
            )).build();

        final EmissionPoints emissionPoints = EmissionPoints.builder()
            .emissionPoints(List.of(EmissionPoint.builder().reference("ep ref 1").description("ep desc 1").build()))
            .build();

        final RegulatedActivities regulatedActivities = RegulatedActivities.builder().regulatedActivities(
            List.of(RegulatedActivity.builder()
                .type(RegulatedActivityType.COMBUSTION)
                .capacity(new BigDecimal(11))
                .capacityUnit(CapacityUnit.KW)
                .build())
        ).build();

        final PermitVariationRequestInfo permitVariationRequestInfo = PermitVariationRequestInfo.builder()
            .id("permitvariation")
            .endDate(permitVariationEndDate.atStartOfDay())
            .metadata(PermitVariationRequestMetadata.builder()
                .logChanges("some log changes")
                .permitConsolidationNumber(10)
                .type(RequestMetadataType.PERMIT_VARIATION)
                .rfiResponseDates(Collections.emptyList())
                .build()
            )
            .build();

        final Permit permit = Permit.builder()
            .sourceStreams(sourceStreams)
            .monitoringApproaches(monitoringApproaches)
            .emissionSources(emissionSources)
            .emissionPoints(emissionPoints)
            .regulatedActivities(regulatedActivities)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .build();

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

        final List<String> permitNotificationIds = List.of("AEMN1-1", "AEMN1-4");

        final AerApplicationSubmitRequestTaskPayload expected = AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .permitType(permitContainer.getPermitType())
            .installationOperatorDetails(installationOperatorDetails)
            .aer(Aer.builder()
                .sourceStreams(sourceStreams)
                .monitoringApproachEmissions(monitoringApproachEmissions)
                .emissionSources(emissionSources)
                .emissionPoints(emissionPoints)
                .regulatedActivities((AerRegulatedActivities.builder()
                    .regulatedActivities(List.of(AerRegulatedActivity.builder()
                        .type(RegulatedActivityType.COMBUSTION)
                        .capacity(new BigDecimal(11))
                        .capacityUnit(CapacityUnit.KW)
                        .build()))
                    .build()))
                .build()
            )
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                    .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                        List.of(
                            CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                            CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build()
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
                .build())
            .monitoringPlanVersions(List.of(monitoringPlanVersionVariation, monitoringPlanVersionIssuance))
            .reportingYear(reportingYear)
            .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(aerRequestQueryService.getApprovedPermitNotificationIdsByAccount(accountId)).thenReturn(permitNotificationIds);

        when(aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId))
            .thenReturn(Optional.of(permitIssuanceEndDate.atStartOfDay()));
        when(permitVariationRequestQueryService.findApprovedPermitVariationRequests(accountId))
            .thenReturn(List.of(permitVariationRequestInfo));
        when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(monitoringPlanVersionIssuance.getPermitId()));
        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionPoints(emissionPoints);
            return null;
        }).when(aerEmissionPointsInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setMonitoringApproachEmissions(monitoringApproachEmissions);
            return null;
        }).when(aerMonitoringApproachEmissionsInitializationService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionSources(emissionSources);
            return null;
        }).when(aerEmissionSourcesInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setSourceStreams(sourceStreams);
            return null;
        }).when(aerSourceStreamsInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setRegulatedActivities(AerRegulatedActivities.builder()
                .regulatedActivities(List.of(AerRegulatedActivity.builder()
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(new BigDecimal(11))
                    .capacityUnit(CapacityUnit.KW)
                    .build()))
                .build());
            return null;
        }).when(aerRegulatedActivitiesInitializationService).initialize(any(Aer.class), eq(permit));

        // Invoke
        AerApplicationSubmitRequestTaskPayload actual = (AerApplicationSubmitRequestTaskPayload)
            initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(aerRequestQueryService, times(1)).getApprovedPermitNotificationIdsByAccount(accountId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void initializePayloadFromRequest() {
        final long accountId = 1L;
        final MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
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
                    .build())
            )
            .build();
        final Year reportingYear = Year.now().minusYears(1);
        final LocalDate permitIssuanceEndDate = LocalDate.now().withYear(reportingYear.getValue());
        final PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                    List.of(
                        CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                        CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build()
                    )
                ))
                .build())
            .permitType(PermitType.GHGE)
            .build();

        final Request request = Request.builder()
            .accountId(1L)
            .payload(AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder()
                    .abbreviations(Abbreviations.builder().exist(false).build())
                    .monitoringApproachEmissions(monitoringApproachEmissions)
                    .build())
                .aerAttachments(Map.of(UUID.randomUUID(), "file"))
                .aerSectionsCompleted(Map.of("section", List.of(true)))
                .permitOriginatedData(permitOriginatedData)
                .build())
            .metadata(AerRequestMetadata.builder().year(reportingYear).build())
            .build();
        final MonitoringPlanVersion monitoringPlanVersionIssuance = MonitoringPlanVersion.builder()
            .permitId("permit")
            .permitConsolidationNumber(1)
            .endDate(LocalDate.MAX).build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("Account name")
            .siteName("Site name")
            .installationLocation(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("ST330000")
                .address(AddressDTO.builder()
                    .line1("line1")
                    .city("city")
                    .country("GB")
                    .postcode("postcode")
                    .build())
                .build())
            .operator("le")
            .operatorType(LegalEntityType.LIMITED_COMPANY)
            .companyReferenceNumber("408812")
            .operatorDetailsAddress(AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GR")
                .postcode("postcode")
                .build())
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder()
                    .exist(true)
                    .abbreviationDefinitions(List.of(
                        AbbreviationDefinition.builder()
                            .abbreviation("abbreviation")
                            .definition("definition")
                            .build()
                    ))
                    .build())
                .build())
            .build();

        final AerApplicationSubmitRequestTaskPayload expected = AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .permitType(permitContainer.getPermitType())
            .installationOperatorDetails(installationOperatorDetails)
            .aer(Aer.builder()
                .abbreviations(Abbreviations.builder().exist(false).build())
                .monitoringApproachEmissions(monitoringApproachEmissions)
                .build())
            .aerAttachments(((AerRequestPayload) request.getPayload()).getAerAttachments())
            .aerSectionsCompleted(((AerRequestPayload) request.getPayload()).getAerSectionsCompleted())
            .permitOriginatedData(permitOriginatedData)
            .reportingYear(reportingYear)
            .monitoringPlanVersions(
                List.of(
                    MonitoringPlanVersion.builder()
                        .permitId("permit")
                        .permitConsolidationNumber(1)
                        .endDate(permitIssuanceEndDate)
                        .build()
                )
            )
            .build();

        when(aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId))
            .thenReturn(Optional.of(permitIssuanceEndDate.atStartOfDay()));
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
//        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(monitoringPlanVersionIssuance.getPermitId()));

        // Invoke
        AerApplicationSubmitRequestTaskPayload actual = (AerApplicationSubmitRequestTaskPayload)
            initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(permitQueryService, times(0)).getPermitContainerByAccountId(accountId);
        verify(aerRequestQueryService, times(0)).getApprovedPermitNotificationIdsByAccount(accountId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        // Invoke
        Set<RequestTaskType> actual = initializer.getRequestTaskTypes();

        // Verify
        assertThat(actual).isEqualTo(Set.of(RequestTaskType.AER_APPLICATION_SUBMIT));
    }
}
