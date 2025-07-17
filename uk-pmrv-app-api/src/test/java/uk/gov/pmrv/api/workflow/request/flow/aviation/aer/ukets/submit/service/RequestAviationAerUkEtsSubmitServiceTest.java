package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerSmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationUkEtsReportableEmissionsUpdateService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSubmitApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.mapper.AviationAerUkEtsSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
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
class RequestAviationAerUkEtsSubmitServiceTest {

    @InjectMocks
    private RequestAviationAerUkEtsSubmitService requestAviationAerUkEtsSubmitService;

    @Mock
    private AviationAerTradingSchemeValidatorService<AviationAerUkEtsContainer> aviationAerUkEtsValidatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    @Mock
    private AviationUkEtsReportableEmissionsUpdateService aviationUkEtsReportableEmissionsUpdateService;

    @Mock
    private AviationAerUkEtsSubmitMapper aviationAerUkEtsSubmitMapper;

    @Test
    void sendToVerifier() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("Verification Section 1", List.of(true));
        AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload =
            AviationAerApplicationRequestVerificationRequestTaskActionPayload.builder()
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerUkEts aviationAerUkEts = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerSmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(30L)
                .numOfFlightsMayAug(40L)
                .numOfFlightsSepDec(50L)
                .totalEmissions(BigDecimal.TEN)
                .build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        EmpUkEtsOriginatedData empOriginatedData = EmpUkEtsOriginatedData.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("designators list")
                    .build())
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingRequired(true)
                .verificationPerformed(true)
                .aer(aviationAerUkEts)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .empOriginatedData(empOriginatedData)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT)
            .request(request)
            .payload(aviationAerSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder().reportingRequired(Boolean.TRUE).aer(aviationAerUkEts).build();
        AviationAerUkEtsSubmittedEmissions submittedEmissions = AviationAerUkEtsSubmittedEmissions.builder()
            .aviationAerTotalEmissions(AviationAerTotalEmissions.builder().totalEmissions(BigDecimal.TEN).build())
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(true)
                .aer(aviationAerUkEts)
                .build();

        when(aviationAerUkEtsSubmitMapper.toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validateAer(aerUkEtsContainer);
        when(aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)).thenReturn(submittedEmissions);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsSubmitMapper
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);

        requestAviationAerUkEtsSubmitService.sendToVerifier(requestVerificationRequestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerUkEts, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);
        assertEquals(empOriginatedData, updatedRequestPayload.getEmpOriginatedData());
        assertEquals(submittedEmissions, updatedRequestPayload.getSubmittedEmissions());

        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validateAer(aerUkEtsContainer);
        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateSubmittedEmissions(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());
    }

    @Test
    void sendToRegulator_when_reporting_required_true() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
                "Verification Section 1", List.of(true),
                "Verification Section 2", List.of(true)
                );
        
        AviationAerUkEtsSubmitApplicationRequestTaskActionPayload requestTaskActionPayload = AviationAerUkEtsSubmitApplicationRequestTaskActionPayload.builder()
        		.verificationSectionsCompleted(verificationSectionsCompleted)
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_PAYLOAD).build();
        
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .verificationReport(verificationReport)
            .build();
        AviationAerRequestMetadata aviationAerRequestMetadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .metadata(aviationAerRequestMetadata)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerUkEts aviationAerUkEts = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerSmallEmittersMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                .numOfFlightsJanApr(30L)
                .numOfFlightsMayAug(40L)
                .numOfFlightsSepDec(50L)
                .totalEmissions(BigDecimal.TEN)
                .build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of("Aer Section 1", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment 1");
        EmpUkEtsOriginatedData empOriginatedData = EmpUkEtsOriginatedData.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
                .flightIdentification(FlightIdentification.builder()
                    .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                    .icaoDesignators("designators list")
                    .build())
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingRequired(true)
                .verificationPerformed(true)
                .aer(aviationAerUkEts)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .empOriginatedData(empOriginatedData)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT)
            .request(request)
            .payload(aviationAerSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder().reportingRequired(Boolean.TRUE).aer(aviationAerUkEts).build();
        AviationAerUkEtsSubmittedEmissions submittedEmissions = AviationAerUkEtsSubmittedEmissions.builder()
            .aviationAerTotalEmissions(AviationAerTotalEmissions.builder().totalEmissions(BigDecimal.TEN).build())
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(true)
                .aer(aviationAerUkEts)
                .verificationReport(verificationReport)
                .build();
        AviationAerUkEtsTotalReportableEmissions totalEmissions = AviationAerUkEtsTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(200))
                .build();

        when(aviationAerUkEtsSubmitMapper.toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validate(aerUkEtsContainer);
        when(aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)).thenReturn(submittedEmissions);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsSubmitMapper
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);
        when(aviationReportableEmissionsService.updateReportableEmissions(aerUkEtsContainer, accountId, false)).thenReturn(totalEmissions);

        requestAviationAerUkEtsSubmitService.sendToRegulator(requestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyEntriesOf(requestTaskActionPayload.getVerificationSectionsCompleted());

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertTrue(updatedRequestPayload.getReportingRequired());
        assertNull(updatedRequestPayload.getReportingObligationDetails());
        assertTrue(updatedRequestPayload.isVerificationPerformed());
        assertEquals(aviationAerUkEts, updatedRequestPayload.getAer());
        assertThat(updatedRequestPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(updatedRequestPayload.getAerSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(aerSectionsCompleted);
        assertEquals(empOriginatedData, updatedRequestPayload.getEmpOriginatedData());
        assertEquals(submittedEmissions, updatedRequestPayload.getSubmittedEmissions());
        assertEquals(submittedRequestActionPayload.getVerificationReport(), updatedRequestPayload.getVerificationReport());

        AviationAerRequestMetadata updatedRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        assertEquals(totalEmissions.getReportableEmissions(), updatedRequestMetadata.getEmissions());

        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, verificationReport, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validate(aerUkEtsContainer);
        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateSubmittedEmissions(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED, appUser.getUserId());
    }

    @Test
    void sendToRegulator_when_reporting_required_false() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();
        
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
                "Verification Section 1", List.of(true),
                "Verification Section 2", List.of(true)
                );
        
        AviationAerUkEtsSubmitApplicationRequestTaskActionPayload requestTaskActionPayload = AviationAerUkEtsSubmitApplicationRequestTaskActionPayload.builder()
        		.verificationSectionsCompleted(verificationSectionsCompleted)
                .payloadType(RequestTaskActionPayloadType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_PAYLOAD).build();

        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .build();
        AviationAerRequestMetadata aviationAerRequestMetadata = AviationAerRequestMetadata.builder().build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .payload(aviationAerUkEtsRequestPayload)
            .accountId(accountId)
            .metadata(aviationAerRequestMetadata)
            .build();

        List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = List.of(
            AviationAerMonitoringPlanVersion.builder().empId("EMP00090").empApprovalDate(LocalDate.of(2023, 1, 1)).empConsolidationNumber(1).build()
        );
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingRequired(false)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                .verificationPerformed(false)
                .aerMonitoringPlanVersions(aerMonitoringPlanVersions)
                .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT)
            .request(request)
            .payload(aviationAerSubmitRequestTaskPayload)
            .build();

        AviationAerUkEtsContainer aerUkEtsContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(Boolean.FALSE)
            .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder().crcoCode("crcoCode").operatorName("operatorName").build();
        AviationAerUkEtsApplicationSubmittedRequestActionPayload submittedRequestActionPayload =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .reportingRequired(false)
                .reportingObligationDetails(AviationAerReportingObligationDetails.builder().noReportingReason("reason").build())
                .build();

        when(aviationAerUkEtsSubmitMapper.toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, null, EmissionTradingScheme.UK_ETS_AVIATION))
            .thenReturn(aerUkEtsContainer);
        doNothing().when(aviationAerUkEtsValidatorService).validate(aerUkEtsContainer);
        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        when(aviationAerUkEtsSubmitMapper
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, null, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD))
            .thenReturn(submittedRequestActionPayload);

        requestAviationAerUkEtsSubmitService.sendToRegulator(requestTaskActionPayload, requestTask, appUser);

        AviationAerUkEtsRequestPayload updatedRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        
        assertThat(updatedRequestPayload.getVerificationSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(verificationSectionsCompleted);

        assertEquals(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertNull(updatedRequestPayload.getAer());
        assertFalse(updatedRequestPayload.getReportingRequired());
        assertNotNull(updatedRequestPayload.getReportingObligationDetails());
        assertNull(updatedRequestPayload.getSubmittedEmissions());

        AviationAerRequestMetadata updatedRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        assertNull((updatedRequestMetadata.getEmissions()));

        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, null, EmissionTradingScheme.UK_ETS_AVIATION);
        verify(aviationAerUkEtsValidatorService, times(1)).validate(aerUkEtsContainer);
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsSubmitMapper, times(1))
            .toAviationAerUkEtsApplicationSubmittedRequestActionPayload(aviationAerSubmitRequestTaskPayload, accountInfo, null, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD);
        verify(requestService, times(1)).addActionToRequest(
            request, submittedRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED, appUser.getUserId());
        verifyNoInteractions(aviationReportableEmissionsService, aviationAerUkEtsEmissionsCalculationService);
    }
}