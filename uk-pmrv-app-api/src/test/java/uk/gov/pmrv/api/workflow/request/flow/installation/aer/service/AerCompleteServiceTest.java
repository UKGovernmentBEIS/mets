package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.NotVerifiedOverallAssessment;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCompleteServiceTest {

    @InjectMocks
    private AerCompleteService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AerService aerService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService<AerVerificationReport> requestVerificationService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void complete() {
        final BigDecimal totalEmissions = BigDecimal.valueOf(1000);
        final String requestId = "requestId";
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final VerificationBodyDetails bodyDetails = getVerificationBodyDetails();
        final AerVerificationReport verificationReport = AerVerificationReport.builder()
                .verificationBodyId(2L)
                .verificationData(AerVerificationData.builder()
                        .overallAssessment(NotVerifiedOverallAssessment.builder()
                                .type(OverallAssessmentType.NOT_VERIFIED)
                                .build())
                        .build())
                .build();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();

        final AerRequestMetadata metadata = AerRequestMetadata.builder()
                .year(Year.now())
                .emissions(BigDecimal.valueOf(1))
                .build();
        final Aer aer = Aer.builder()
                .abbreviations(Abbreviations.builder().exist(false).build())
                .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .accountId(1L)
                .verificationBodyId(2L)
                .metadata(metadata)
                .payload(AerRequestPayload.builder()
                        .verificationPerformed(true)
                        .verificationReport(verificationReport)
                        .aer(aer)
                        .permitOriginatedData(permitOriginatedData)
                        .build())
                .build();

        final AerSubmitParams params = AerSubmitParams.builder()
                .accountId(request.getAccountId())
                .aerContainer(AerContainer.builder()
                        .aer(aer)
                        .verificationReport(verificationReport)
                        .reportingYear(Year.now())
                        .installationOperatorDetails(installationOperatorDetails)
                        .permitOriginatedData(permitOriginatedData)
                        .build())
                .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);
        when(requestVerificationService.getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
                .thenReturn(bodyDetails);
        when(aerService.submitAer(params))
                .thenReturn(totalEmissions);

        // Invoke
        service.complete(requestId);

        // Verify
        verify(requestService, times(1))
                .findRequestById(requestId);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestVerificationService, times(1))
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId());
        verify(aerService, times(1))
                .submitAer(params);

        assertThat(request.getMetadata()).isInstanceOf(AerRequestMetadata.class);
        Assertions.assertEquals(((AerRequestMetadata) request.getMetadata()).getOverallAssessmentType(), OverallAssessmentType.NOT_VERIFIED);
        Assertions.assertEquals(((AerRequestMetadata) request.getMetadata()).getEmissions(), totalEmissions);
    }

    @Test
    void addRequestAction() {
        final String requestId = "requestId";
        final String regulatorReviewer = "regulatorReviewer";
        final Year reportingYear = Year.of(2022);
        final UUID attachmentId = UUID.randomUUID();

        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final VerificationBodyDetails bodyDetails = getVerificationBodyDetails();
        final AerVerificationReport verificationReport = AerVerificationReport.builder()
                .verificationBodyId(2L)
                .build();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final Aer aer = Aer.builder()
                .abbreviations(Abbreviations.builder().exist(false).build())
                .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
                .build();
        final Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
                AerReviewGroup.ADDITIONAL_INFORMATION,
                AerDataReviewDecision.builder()
                        .reviewDataType(AerReviewDataType.AER_DATA)
                        .type(AerDataReviewDecisionType.ACCEPTED)
                        .build()

        );

        final Request request = Request.builder()
                .id(requestId)
                .accountId(1L)
                .verificationBodyId(2L)
                .metadata(AerRequestMetadata.builder().year(reportingYear).build())
                .payload(AerRequestPayload.builder()
                        .regulatorReviewer(regulatorReviewer)
                        .verificationPerformed(true)
                        .verificationReport(verificationReport)
                        .aer(aer)
                        .permitOriginatedData(permitOriginatedData)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .verificationAttachments(Map.of(attachmentId, "fileName"))
                        .build())
                .build();

        final AerApplicationCompletedRequestActionPayload actionPayload = AerApplicationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AER_APPLICATION_COMPLETED_PAYLOAD)
                .reportingYear(reportingYear)
                .aer(aer)
                .installationOperatorDetails(installationOperatorDetails)
                .permitOriginatedData(permitOriginatedData)
                .monitoringPlanVersions(monitoringPlanVersions)
                .verificationReport(verificationReport)
                .reviewGroupDecisions(reviewGroupDecisions)
                .verificationAttachments(Map.of(attachmentId, "fileName"))
                .build();

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);
        when(requestVerificationService.getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
                .thenReturn(bodyDetails);

        // Invoke
        service.addRequestAction(requestId);

        // Verify
        verify(requestService, times(1))
                .findRequestById(requestId);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestVerificationService, times(1))
                .getVerificationBodyDetails(verificationReport, request.getVerificationBodyId());
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.AER_APPLICATION_COMPLETED, regulatorReviewer);
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
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
    }

    private VerificationBodyDetails getVerificationBodyDetails() {
        return VerificationBodyDetails.builder()
                .name("name2")
                .accreditationReferenceNumber("accRefNum2")
                .address(AddressDTO.builder()
                        .line1("line2")
                        .city("city2")
                        .country("GR")
                        .postcode("postcode2")
                        .build())
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
                .build();
    }

    private PermitOriginatedData getPermitOriginatedData() {
        return PermitOriginatedData.builder()
                .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                        .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                                List.of(
                                        CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                                        CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build()
                                )
                        ))
                        .build())
                .permitNotificationIds(List.of("AEMN1-1", "AEMN1-4"))
                .permitType(PermitType.GHGE)
                .build();
    }

    private List<MonitoringPlanVersion> getMonitoringPlanVersions() {
        return List.of(MonitoringPlanVersion.builder().permitId("permitId").permitConsolidationNumber(1)
                .endDate(LocalDate.now().minusYears(2)).build());
    }
}
