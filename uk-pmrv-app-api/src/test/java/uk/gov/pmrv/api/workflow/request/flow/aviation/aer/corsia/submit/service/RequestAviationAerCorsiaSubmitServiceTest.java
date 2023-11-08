package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerifiedSatisfactoryDecision;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaCertDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaFlightType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.mapper.AviationAerCorsiaSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAerCorsiaSubmitServiceTest {

    @InjectMocks
    private RequestAviationAerCorsiaSubmitService requestAviationAerCorsiaSubmitService;

    @Mock
    private AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> aviationAerCorsiaValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    @Mock
    private AviationAerCorsiaSubmitMapper aviationAerCorsiaSubmitMapper;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Test
    void sendToVerifier() {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("Verification Section 1", List.of(true));
        AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload =
                AviationAerApplicationRequestVerificationRequestTaskActionPayload.builder()
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .build();

        AviationAerCorsiaRequestPayload aviationAerUkEtsRequestPayload = AviationAerCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                .build();
        Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA)
                .payload(aviationAerUkEtsRequestPayload)
                .accountId(accountId)
                .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
                AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerCorsia aviationAerCorsia = AviationAerCorsia.builder().build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        EmpCorsiaOriginatedData empOriginatedData = EmpCorsiaOriginatedData.builder()
                .operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("operatorName")
                        .flightIdentification(FlightIdentification.builder()
                                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                .icaoDesignators("designators list")
                                .build())
                        .build())
                .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                        .reportingRequired(true)
                        .verificationPerformed(true)
                        .aer(aviationAerCorsia)
                        .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                        .aerSectionsCompleted(aerSectionsCompleted)
                        .aerAttachments(aerAttachments)
                        .empOriginatedData(empOriginatedData)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT)
                .request(request)
                .payload(aviationAerSubmitRequestTaskPayload)
                .build();

        AviationAerCorsiaContainer aerCorsiaContainer = AviationAerCorsiaContainer.builder().aer(aviationAerCorsia).build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerCorsiaApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
                AviationAerCorsiaApplicationSubmittedRequestActionPayload.builder()
                        .reportingRequired(true)
                        .aer(aviationAerCorsia)
                        .build();

        when(aviationAerCorsiaSubmitMapper.toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA))
                .thenReturn(aerCorsiaContainer);
        doNothing().when(aviationAerCorsiaValidatorService).validateAer(aerCorsiaContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaSubmitMapper
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD))
                .thenReturn(submittedRequestActionPayload);

        requestAviationAerCorsiaSubmitService.sendToVerifier(requestVerificationRequestTaskActionPayload, requestTask, pmrvUser);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerCorsia, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);
        assertEquals(empOriginatedData, updatedRequestPayload.getEmpOriginatedData());

        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA);
        verify(aviationAerCorsiaValidatorService, times(1)).validateAer(aerCorsiaContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
                request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER, pmrvUser.getUserId());
    }

    @Test
    void sendToRegulator_when_reporting_required_true() {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationData(AviationAerCorsiaVerificationData.builder()
                        .overallDecision(AviationAerVerifiedSatisfactoryDecision.builder()
                                .type(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY)
                                .build())
                        .build())
                .build();
        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = AviationAerCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                .verificationReport(verificationReport)
                .build();
        AviationAerCorsiaRequestMetadata aviationAerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().build();
        Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA)
                .payload(aviationAerCorsiaRequestPayload)
                .accountId(accountId)
                .metadata(aviationAerRequestMetadata)
                .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
                AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerCorsia aviationAerCorsia = AviationAerCorsia.builder()
                .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                        .certUsed(Boolean.TRUE)
                        .certDetails(AviationAerCorsiaCertDetails.builder()
                                .flightType(AviationAerCorsiaFlightType.ALL_INTERNATIONAL_FLIGHTS)
                                .publicationYear(Year.of(2023))
                                .build())
                        .build())
                .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        EmpCorsiaOriginatedData empOriginatedData = EmpCorsiaOriginatedData.builder()
                .operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("operatorName")
                        .flightIdentification(FlightIdentification.builder()
                                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                .icaoDesignators("designators list")
                                .build())
                        .build())
                .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                        .reportingRequired(true)
                        .verificationPerformed(true)
                        .aer(aviationAerCorsia)
                        .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                        .aerSectionsCompleted(aerSectionsCompleted)
                        .aerAttachments(aerAttachments)
                        .empOriginatedData(empOriginatedData)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT)
                .request(request)
                .payload(aviationAerSubmitRequestTaskPayload)
                .build();

        AviationAerCorsiaContainer aerCorsiaContainer = AviationAerCorsiaContainer.builder()
                .reportingRequired(Boolean.TRUE)
                .aer(aviationAerCorsia)
                .verificationReport(verificationReport)
                .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerCorsiaApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
                AviationAerCorsiaApplicationSubmittedRequestActionPayload.builder()
                        .reportingRequired(true)
                        .aer(aviationAerCorsia)
                        .build();

        final BigDecimal allFlightsEmissions = BigDecimal.valueOf(1200);
        final BigDecimal offsetFlightsEmissions = BigDecimal.valueOf(900);
        final BigDecimal emissionsReductionClaim = BigDecimal.valueOf(300);
        final AviationAerCorsiaTotalReportableEmissions totalEmissions = AviationAerCorsiaTotalReportableEmissions.builder()
                .reportableEmissions(allFlightsEmissions)
                .reportableOffsetEmissions(offsetFlightsEmissions)
                .reportableReductionClaimEmissions(emissionsReductionClaim)
                .build();
        final AviationAerCorsiaSubmittedEmissions submittedEmissions = AviationAerCorsiaSubmittedEmissions.builder()
                .totalEmissions(AviationAerCorsiaTotalEmissions.builder()
                        .allFlightsEmissions(allFlightsEmissions)
                        .offsetFlightsEmissions(offsetFlightsEmissions)
                        .emissionsReductionClaim(emissionsReductionClaim)
                        .build())
                .build();

        when(aviationAerCorsiaSubmitMapper.toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.CORSIA))
                .thenReturn(aerCorsiaContainer);
        when(aviationAerCorsiaEmissionsCalculationService.calculateSubmittedEmissions(aerCorsiaContainer))
                .thenReturn(submittedEmissions);
        doNothing().when(aviationAerCorsiaValidatorService).validate(aerCorsiaContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaSubmitMapper
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD))
                .thenReturn(submittedRequestActionPayload);
        when(aviationReportableEmissionsService.updateReportableEmissions(aerCorsiaContainer, accountId)).thenReturn(totalEmissions);

        requestAviationAerCorsiaSubmitService.sendToRegulator(requestTask, pmrvUser);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerCorsia, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getTotalEmissionsProvided()).isEqualTo(allFlightsEmissions);
        assertThat(updatedRequestPayload.getTotalOffsetEmissionsProvided()).isEqualTo(offsetFlightsEmissions);
        assertEquals(empOriginatedData, updatedRequestPayload.getEmpOriginatedData());

        AviationAerCorsiaRequestMetadata updatedRequestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        assertEquals(allFlightsEmissions, updatedRequestMetadata.getEmissions());
        assertEquals(offsetFlightsEmissions, updatedRequestMetadata.getTotalEmissionsOffsettingFlights());
        assertEquals(emissionsReductionClaim, updatedRequestMetadata.getTotalEmissionsClaimedReductions());
        assertEquals(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY, updatedRequestMetadata.getOverallAssessmentType());

        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.CORSIA);
        verify(aviationAerCorsiaValidatorService, times(1)).validate(aerCorsiaContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
                request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED, pmrvUser.getUserId());
        verify(aviationReportableEmissionsService, times(1))
                .updateReportableEmissions(aerCorsiaContainer, accountId);
    }

    @Test
    void sendToRegulator_when_reporting_required_false() {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = AviationAerCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                .build();
        AviationAerCorsiaRequestMetadata aviationAerRequestMetadata = AviationAerCorsiaRequestMetadata.builder().build();
        Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA)
                .payload(aviationAerCorsiaRequestPayload)
                .accountId(accountId)
                .metadata(aviationAerRequestMetadata)
                .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
                AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
                AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                        .reportingRequired(false)
                        .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                        .verificationPerformed(false)
                        .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT)
                .request(request)
                .payload(aviationAerSubmitRequestTaskPayload)
                .build();

        AviationAerCorsiaContainer aerCorsiaContainer = AviationAerCorsiaContainer.builder()
                .reportingRequired(Boolean.FALSE)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerCorsiaApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
                AviationAerCorsiaApplicationSubmittedRequestActionPayload.builder()
                        .reportingRequired(false)
                        .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                        .build();

        when(aviationAerCorsiaSubmitMapper.toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, null, EmissionTradingScheme.CORSIA))
                .thenReturn(aerCorsiaContainer);
        doNothing().when(aviationAerCorsiaValidatorService).validate(aerCorsiaContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerCorsiaSubmitMapper
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD))
                .thenReturn(submittedRequestActionPayload);

        requestAviationAerCorsiaSubmitService.sendToRegulator(requestTask, pmrvUser);

        AviationAerCorsiaRequestPayload updatedRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertNull(updatedRequestPayload.getAer());
        assertFalse(updatedRequestPayload.getReportingRequired());
        assertNotNull(updatedRequestPayload.getReportingObligationDetails());

        AviationAerCorsiaRequestMetadata updatedRequestMetadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        assertNull((updatedRequestMetadata.getEmissions()));

        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, null, EmissionTradingScheme.CORSIA);
        verify(aviationAerCorsiaValidatorService, times(1)).validate(aerCorsiaContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerCorsiaSubmitMapper, times(1))
                .toAviationAerCorsiaApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
                request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED, pmrvUser.getUserId());
        verifyNoInteractions(aviationReportableEmissionsService, aviationAerCorsiaEmissionsCalculationService);
    }
}
