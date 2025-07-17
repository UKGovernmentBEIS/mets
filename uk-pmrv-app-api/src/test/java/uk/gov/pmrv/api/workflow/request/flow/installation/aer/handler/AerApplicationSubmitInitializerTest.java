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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
        final Year reportingYear = Year.now().minusYears(1);
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();

        final Request request = Request.builder()
            .accountId(1L)
            .payload(AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().build())
                .permitOriginatedData(PermitOriginatedData.builder().permitType(PermitType.GHGE).build())
                .monitoringPlanVersions(List.of(MonitoringPlanVersion.builder().build()))
                .build())
            .metadata(AerRequestMetadata.builder().year(reportingYear).build()).build();

        final AerApplicationSubmitRequestTaskPayload expected = AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .permitType(PermitType.GHGE)
            .installationOperatorDetails(installationOperatorDetails)
            .aer(((AerRequestPayload) request.getPayload()).getAer())
            .permitOriginatedData(((AerRequestPayload) request.getPayload()).getPermitOriginatedData())
            .monitoringPlanVersions(((AerRequestPayload) request.getPayload()).getMonitoringPlanVersions())
            .reportingYear(reportingYear)
            .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);

        // Invoke
        AerApplicationSubmitRequestTaskPayload actual = (AerApplicationSubmitRequestTaskPayload)
            initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void initializePayloadFromRequest() {
        final long accountId = 1L;
        final Year reportingYear = Year.now().minusYears(1);

        final Request request = Request.builder()
            .accountId(1L)
            .payload(AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().build())
                .permitOriginatedData(PermitOriginatedData.builder().permitType(PermitType.GHGE).build())
                .aerAttachments(Map.of(UUID.randomUUID(), "file"))
                .aerSectionsCompleted(Map.of("section", List.of(true)))
                .build())
            .metadata(AerRequestMetadata.builder().year(reportingYear).build())
            .build();

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
            .build();

        final AerApplicationSubmitRequestTaskPayload expected = AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .permitType(permitContainer.getPermitType())
            .installationOperatorDetails(installationOperatorDetails)
            .aer(((AerRequestPayload) request.getPayload()).getAer())
            .aerAttachments(((AerRequestPayload) request.getPayload()).getAerAttachments())
            .aerSectionsCompleted(((AerRequestPayload) request.getPayload()).getAerSectionsCompleted())
            .permitOriginatedData(((AerRequestPayload) request.getPayload()).getPermitOriginatedData())
            .reportingYear(reportingYear)
            .monitoringPlanVersions(((AerRequestPayload) request.getPayload()).getMonitoringPlanVersions())
            .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);

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
