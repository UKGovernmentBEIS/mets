package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRNotVerifiedOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitServiceTest {

    @InjectMocks
    private ALRSubmitService service;

    @Mock
    private ALRValidationService alrValidationService;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    private final long accountId = 1L;
    private final UUID attachmentId = UUID.randomUUID();
    private final UUID verificationAttachmentId = UUID.randomUUID();
    private final AppUser appUser = AppUser.builder().userId("userId").build();

    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("test",false);

        final ALRApplicationSubmitRequestTaskPayload expectedTaskPayload =
                ALRApplicationSubmitRequestTaskPayload
                        .builder()
                        .verificationPerformed(true)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final ALRApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                ALRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_APPLICATION_SAVE_PAYLOAD)
                        .alr(ALR.builder().build())
                        .alrSectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getAlr(), expectedTaskActionPayload.getAlr());
        assertEquals(expectedTaskPayload.getAlrSectionsCompleted(), expectedTaskActionPayload.getAlrSectionsCompleted());
    }

    @Test
    void submitToVerifier() {
        UUID alrFile = UUID.randomUUID();
        UUID verificationAttachmentId = UUID.randomUUID();

        ALR alr = ALR.builder().alrFile(alrFile).build();
        ALRVerificationReport report = buildVerificationReport();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachmentId, "test");

        ALRRequestPayload payload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .verificationReport(report)
                .verificationAttachments(verificationAttachments)
                .reportingYear(Year.of(2025))
                .build();

        Request request = buildRequest(payload);

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(alr)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = buildRequestTask(request, taskPayload);

        Request expectedRequest = Request.builder()
                .accountId(accountId)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(alr)
                        .verificationPerformed(true)
                        .verificationReport(report)
                        .verificationAttachments(verificationAttachments)
                        .alrFileVersion(2)
                        .reportingYear(Year.of(2025))
                        .build())
                .metadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).build())
                .build();

        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .alr(alr)
                .verificationPerformed(true)
                .verificationReport(report)
                .installationOperatorDetails(installationOperatorDetails)
                .verificationAttachments(verificationAttachments)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        service.submitToVerifier(ALRApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(), requestTask, appUser);

        verify(alrValidationService).validateALR(alr);
        verify(installationOperatorDetailsQueryService).getInstallationOperatorDetails(accountId);
        verify(requestService).addActionToRequest(eq(request), eq(expectedActionPayload),
                eq(RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER), eq(appUser.getUserId()));

        assertThat(request.getPayload()).isEqualTo(expectedRequest.getPayload());
    }

    @Test
    void submitToRegulator() {
        final long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        UUID alrFileId = UUID.randomUUID();
        ALR alr = ALR.builder().alrFile(alrFileId).build();

        ALRVerificationReport alrVerificationReport = ALRVerificationReport.builder()
                .verificationData(ALRVerificationData
                        .builder()
                        .overallAssessment(ALRNotVerifiedOverallVerificationAssessment.builder()
                                .type(OverallAssessmentType.NOT_VERIFIED)
                                .build())
                        .build())
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .verificationReport(alrVerificationReport)
                        .reportingYear(Year.of(2025))
                        .build())
                .metadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .build())
                .build();

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(alr)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request expectedRequest = Request.builder()
                .accountId(accountId)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(alr)
                        .verificationPerformed(true)
                        .verificationReport(alrVerificationReport)
                        .alrFileVersion(2)
                        .reportingYear(Year.of(2025))
                        .build())
                .metadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .build())
                .build();

        ALRApplicationSubmittedRequestActionPayload actionPayload =
                ALRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                        .alr(alr)
                        .verificationPerformed(true)
                        .verificationReport(alrVerificationReport)
                        .installationOperatorDetails(installationOperatorDetails)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        service.submitToRegulator(requestTask, appUser);

        verify(alrValidationService, times(1)).validateALR(alr);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.ALR_APPLICATION_SENT_TO_REGULATOR, appUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(expectedRequest.getPayload());
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_verificationPerformed_setsVerificationFields() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, true, false);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationPerformed(true)
                .verificationReport(report)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        ALRApplicationSubmittedRequestActionPayload result = service.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        assertThat(result).isEqualTo(expectedPayload);
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_verificationNotPerformed_doesNotSetVerificationFields() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, false, false);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationPerformed(false)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        ALRApplicationSubmittedRequestActionPayload result = service.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        assertThat(result).isEqualTo(expectedPayload);
        assertThat(result.getVerificationReport()).isNull();
        assertThat(result.getVerificationAttachments()).isEmpty();
    }

    @Test
    void submitALR_requestPayloadIsNull_incrementFileVersion() {
        UUID alrFileId = UUID.randomUUID();
        ALR alr = ALR.builder().alrFile(alrFileId).build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, true, true);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .verificationReport(report)
                .verificationPerformed(true)
                .build();

        service.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted(), false);

        verify(requestService).addActionToRequest(request, requestActionPayload,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(requestPayload.getAlr()).isEqualTo(alr);
        assertThat(requestPayload.getAlrAttachments()).isEqualTo(taskPayload.getAlrAttachments());
        assertThat(requestPayload.getAlrSectionsCompleted()).isEqualTo(taskPayload.getAlrSectionsCompleted());
        assertThat(requestPayload.getAlrFileVersion()).isEqualTo(2);
        assertThat(requestPayload.isVerificationPerformed()).isTrue();
    }

    @Test
    void submitALR_ALRInTaskPayloadIsNull_doNotIncrementFileVersion() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, true, true);
        taskPayload.setAlr(null);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .verificationReport(report)
                .verificationPerformed(true)
                .build();

        service.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted(), false);

        verify(requestService).addActionToRequest(request, requestActionPayload,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(requestPayload.getAlr()).isNull();
        assertThat(requestPayload.getAlrAttachments()).isEqualTo(taskPayload.getAlrAttachments());
        assertThat(requestPayload.getAlrSectionsCompleted()).isEqualTo(taskPayload.getAlrSectionsCompleted());
        assertThat(requestPayload.getAlrFileVersion()).isEqualTo(1);
        assertThat(requestPayload.isVerificationPerformed()).isTrue();
    }

    @Test
    void submitALR_requestPayloadIsNotNullAndFileIsChanged_incrementFileVersion() {

        UUID requestFile = UUID.randomUUID();
        UUID taskFile = UUID.randomUUID();


        ALR requestALR = ALR
                .builder()
                .alrFile(requestFile)
                .build();

        ALR taskALR = ALR
                .builder()
                .alrFile(taskFile)
                .build();

        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(requestALR, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(taskALR, true, true);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(taskALR)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .verificationReport(report)
                .verificationPerformed(true)
                .build();

        service.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted(), false);

        verify(requestService).addActionToRequest(request, requestActionPayload,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(requestPayload.getAlr()).isEqualTo(taskALR);
        assertThat(requestPayload.getAlrAttachments()).isEqualTo(taskPayload.getAlrAttachments());
        assertThat(requestPayload.getAlrSectionsCompleted()).isEqualTo(taskPayload.getAlrSectionsCompleted());
        assertThat(requestPayload.getAlrFileVersion()).isEqualTo(2);
        assertThat(requestPayload.isVerificationPerformed()).isTrue();
    }

    @Test
    void submitALR_requestPayloadIsNotNullAndFileIsNotChanged_doNotIncrementFileVersion() {

        UUID requestFile = UUID.randomUUID();

        ALR requestALR = ALR
                .builder()
                .alrFile(requestFile)
                .build();

        ALR taskALR = ALR
                .builder()
                .alrFile(requestFile)
                .build();

        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(requestALR, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(taskALR, true, true);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(taskALR)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .verificationReport(report)
                .verificationPerformed(true)
                .build();

        service.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted(), false);

        verify(requestService).addActionToRequest(request, requestActionPayload,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(requestPayload.getAlr()).isEqualTo(taskALR);
        assertThat(requestPayload.getAlrAttachments()).isEqualTo(taskPayload.getAlrAttachments());
        assertThat(requestPayload.getAlrSectionsCompleted()).isEqualTo(taskPayload.getAlrSectionsCompleted());
        assertThat(requestPayload.getAlrFileVersion()).isEqualTo(1);
        assertThat(requestPayload.isVerificationPerformed()).isTrue();
    }

    @Test
    void notifyOperator() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPaylod =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .allowances(20)
                        .build(),
                ALRPreliminaryAllocation.builder().year(Year.of(2023))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build()
        );
        final ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome = ALRApplicationRegulatorReviewOutcome.builder()
                .determination(ALRClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED_ALR)
                        .build())
                .allocations(allocations)
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(ALRRequestPayload.builder()
                                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .alrSectionsCompleted(sectionsCompleted)
                        .alrAttachments(attachments)
                        .build())
                .build();

        final ALRRequestPayload expectedPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .decisionNotification(decisionNotification)
                .regulatorReviewOutcome(regulatorReviewOutcome)
                .alrSectionsCompleted(sectionsCompleted)
                .alrAttachments(attachments)
                .build();

        // Invoke
        service.notifyOperator(requestTask, taskActionPaylod);

        // Verify
        ALRRequestPayload updatedPayload = (ALRRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }

    @Test
    void complete() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUserId"))
                .signatory("regulatorUserId")
                .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPaylod =
                NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder().year(Year.of(2022))
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .allowances(20)
                        .build(),
                ALRPreliminaryAllocation.builder().year(Year.of(2023))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .allowances(10)
                        .build()
        );
        final ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome = ALRApplicationRegulatorReviewOutcome.builder()
                .determination(ALRClosedDetermination.builder()
                        .type(DoalDeterminationType.CLOSED_ALR)
                        .build())
                .allocations(allocations)
                .build();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(ALRRequestPayload.builder()
                                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .alrSectionsCompleted(sectionsCompleted)
                        .alrAttachments(attachments)
                        .build())
                .build();

        final ALRRequestPayload expectedPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .decisionNotification(decisionNotification)
                .regulatorReviewOutcome(regulatorReviewOutcome)
                .alrSectionsCompleted(sectionsCompleted)
                .alrAttachments(attachments)
                .build();

        // Invoke
        service.notifyOperator(requestTask, taskActionPaylod);

        // Verify
        ALRRequestPayload updatedPayload = (ALRRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);



    }

    @Test
    void addProceededToAuthorityRequestAction() {
        final String requestId = "ALR";
        final Long accountId = 1L;
        final String regulatorAssignee = "regulatorAssignee";

        final ALR alr = ALR.builder()
                .build();

        final FileInfoDTO file = FileInfoDTO.builder()
                .name("Activity_level_determination_preliminary_allocation_letter.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();


        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operatorUser"))
                .signatory("regulatorUser")
                .build();

        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        final ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .regulatorAssignee(regulatorAssignee)
                .alr(alr)
                .decisionNotification(decisionNotification)
                .officialNotice(file)
                .alrAttachments(attachments)
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUser", RequestActionUserInfo.builder().name("operator1").roleCode("admin").build(),
                "regulatorUser", RequestActionUserInfo.builder().name("regulator").roleCode("admin").build()
        );

        final ALRApplicationProceededToAuthorityRequestActionPayload actionPayload =
                ALRApplicationProceededToAuthorityRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)
                        .alr(alr)
                        .decisionNotification(decisionNotification)
                        .usersInfo(usersInfo)
                        .officialNotice(file)
                        .alrAttachments(attachments)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operatorUser"), "regulatorUser", request))
                .thenReturn(usersInfo);

        // Invoke
        service.addProceededToAuthorityRequestAction(requestId);

        // Verify
        verify(requestService).findRequestById(requestId);
        verify(requestActionUserInfoResolver)
                .getUsersInfo(Set.of("operatorUser"), "regulatorUser", request);
        verify(requestService).addActionToRequest(
                eq(request),
                eq(actionPayload),
                eq(RequestActionType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY),
                eq(regulatorAssignee));
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        AddressDTO address = AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GB")
                .postcode("postcode")
                .build();

        return InstallationOperatorDetails.builder()
                .installationName("Install")
                .siteName("Site")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("GR")
                        .address(address)
                        .build())
                .operator("operator")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("123456")
                .operatorDetailsAddress(address)
                .build();
    }

    private ALRVerificationReport buildVerificationReport() {
        return ALRVerificationReport.builder()
                .verificationData(ALRVerificationData.builder().build())
                .build();
    }

    private ALRRequestPayload buildRequestPayload(ALR alr, ALRVerificationReport report, boolean performed) {
        return ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .alr(alr)
                .verificationPerformed(performed)
                .verificationReport(report)
                .reportingYear(Year.of(2025))
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .alrFileVersion(1)
                .build();
    }

    private ALRApplicationSubmitRequestTaskPayload buildTaskPayload(ALR alr, boolean performed, boolean withSections) {
        var builder = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(alr)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .verificationPerformed(performed)
                .alrFileVersion(1);

        if (withSections)
            builder.alrSectionsCompleted(Map.of("test", true));

        return builder.build();
    }

    private Request buildRequest(ALRRequestPayload payload) {
        return Request.builder()
                .accountId(accountId)
                .payload(payload)
                .metadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).build())
                .build();
    }

    private RequestTask buildRequestTask(Request request, ALRApplicationSubmitRequestTaskPayload taskPayload) {
        return RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final String selectedPeerReviewer = "selectedPeerReviewer";

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        ALR alr = ALR.builder().build();

        Request request = Request.builder()
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.ALR_APPLICATION_PEER_REVIEW_PAYLOAD)
                        .alr(alr)
                        .alrSectionsCompleted(sectionsCompleted)
                        .alrAttachments(attachments)
                        .build())
                .build();

        final ALRRequestPayload expectedPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .alr(alr)
                .alrSectionsCompleted(sectionsCompleted)
                .alrAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .regulatorReviewer(user.getUserId())
                .build();

        service.requestPeerReview(requestTask, selectedPeerReviewer, user);

        ALRRequestPayload updatedPayload = (ALRRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }
}
