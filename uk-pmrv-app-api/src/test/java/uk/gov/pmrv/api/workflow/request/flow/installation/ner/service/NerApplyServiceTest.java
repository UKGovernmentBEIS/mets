package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;


import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerifiedSatisfactoryOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NerApplyServiceTest {

    @InjectMocks
    private NerApplyService service;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private NERValidationService nerValidationService;

    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {

        final NerApplicationSubmitRequestTaskPayload taskPayload =
            NerApplicationSubmitRequestTaskPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        final Map<String, Boolean> sectionsCompleted = Map.of("section1", true);

        final NER ner = NER.builder().build();
        final int nerFileVersion = 1;

        final NerSaveApplicationRequestTaskActionPayload taskActionPayload =
            NerSaveApplicationRequestTaskActionPayload.builder()
                .nerSectionsCompleted(sectionsCompleted)
                .ner(ner)
                .nerFileVersion(nerFileVersion)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(sectionsCompleted, taskPayload.getNerSectionsCompleted());
        assertEquals(ner, taskPayload.getNer());
        assertEquals(nerFileVersion, taskPayload.getNerFileVersion());
    }

    @Test
    void submitToVerifier_happyPath() {
        // given
        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(NER.builder().build())
                        .nerAttachments(Map.of(UUID.randomUUID(), "file.pdf"))
                        .build();

        Request request = Request.builder()
                .payload(NerRequestPayload.builder().ner(NER.builder().build()).build())
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToVerifier(
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(),
                requestTask,
                appUser,
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER
        );

        // then
        verify(nerValidationService).validateNer(taskPayload.getNer());
        verify(requestService).addActionToRequest(
                eq(request),
                any(NERApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER),
                eq("user")
        );
    }

    @Test
    void submitToVerifier_whenFileExists_shouldValidateFileName() {
        // given
        UUID fileId = UUID.randomUUID();

        NERFiles nerFiles = NERFiles.builder()
                .file(fileId)
                .build();

        NER ner = NER.builder()
                .nerFiles(nerFiles)
                .build();

        Map<UUID, String> attachments = Map.of(fileId, "NER-00026-11-v1-uploaded by Operator-Test.pdf");

        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(ner)
                        .nerAttachments(attachments)
                        .build();

        Request request = Request.builder()
                .payload(NerRequestPayload.builder().ner(NER.builder().build()).build())
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToVerifier(
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(),
                requestTask,
                AppUser.builder().userId("user").build(),
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER
        );

        // then
        verify(nerValidationService).validateNerFileName(anyString());
    }

    @Test
    void submitToVerifier_whenFileChanged_shouldIncrementVersion() {
        // given
        UUID oldFile = UUID.randomUUID();
        UUID newFile = UUID.randomUUID();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .ner(NER.builder()
                        .nerFiles(NERFiles.builder().file(oldFile).build())
                        .build())
                .build();

        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(NER.builder()
                                .nerFiles(NERFiles.builder().file(newFile).build())
                                .build())
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToVerifier(
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(),
                requestTask,
                AppUser.builder().userId("user").build(),
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER
        );

        // then
        assertEquals(2, requestPayload.getNerFileVersion());
    }

    @Test
    void submitToVerifier_whenSameFile_shouldNotIncrementVersion() {
        // given
        UUID file = UUID.randomUUID();

        NerRequestPayload requestPayload = spy(NerRequestPayload.builder()
                .ner(NER.builder()
                        .nerFiles(NERFiles.builder().file(file).build())
                        .build())
                .build());

        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(NER.builder()
                                .nerFiles(NERFiles.builder().file(file).build())
                                .build())
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToVerifier(
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(),
                requestTask,
                AppUser.builder().userId("user").build(),
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER
        );

        // then
        verify(requestPayload, never()).incrementNerFileVersion();
    }

    @Test
    void submitToVerifier_whenNoFile_shouldNotValidateFileName() {
        // given
        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(NER.builder().build())
                        .build();

        Request request = Request.builder()
                .payload(NerRequestPayload.builder().ner(NER.builder().build()).build())
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToVerifier(
                NERApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(),
                requestTask,
                AppUser.builder().userId("user").build(),
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER
        );

        // then
        verify(nerValidationService, never()).validateNerFileName(any());
    }

    @Test
    void submitToRegulator_happyPath() {
        // given
        UUID fileId = UUID.randomUUID();

        NERFiles nerFiles = NERFiles.builder()
                .file(fileId)
                .build();

        NER ner = NER.builder()
                .nerFiles(nerFiles)
                .build();

        Map<UUID, String> attachments = Map.of(fileId, "file.pdf");
        Map<String, Boolean> sectionsCompleted = Map.of("section1", true);

        NERVerificationReport verificationReport = NERVerificationReport.builder().verificationData(NERVerificationData.builder().overallAssessment(NERVerifiedSatisfactoryOverallVerificationAssessment.builder().build()).build()).build();

        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .ner(NER.builder().build())
                .verificationReport(verificationReport)
                .build();

        NERRequestMetadata nerRequestMetadata = NERRequestMetadata.builder().type(RequestMetadataType.NER).build();

        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(ner)
                        .nerAttachments(attachments)
                        .nerSectionsCompleted(sectionsCompleted)
                        .verificationPerformed(true)
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(nerRequestMetadata)
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToRegulator(requestTask, appUser, RequestActionType.NER_APPLICATION_SUBMITTED);

        // then
        verify(nerValidationService).validateVerificationReport(verificationReport);
        verify(nerValidationService).validateNer(ner);
        verify(nerValidationService).validateNerFileName("file.pdf");

        verify(requestService).addActionToRequest(
                eq(request),
                any(NERApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.NER_APPLICATION_SUBMITTED),
                eq("user")
        );

        assertEquals(ner, requestPayload.getNer());
        assertEquals(attachments, requestPayload.getNerAttachments());
        assertEquals(sectionsCompleted, requestPayload.getNerSectionsCompleted());
        assertTrue(requestPayload.isVerificationPerformed());
    }

    @Test
    void submitToRegulator_whenVerificationNotPerformed_shouldDeleteReport() {
        // given
        NerRequestPayload requestPayload = NerRequestPayload.builder()
                .ner(NER.builder().nerFiles(NERFiles.builder().build()).build())
                .verificationReport(NERVerificationReport.builder().build())
                .build();

        NerApplicationSubmitRequestTaskPayload taskPayload =
                NerApplicationSubmitRequestTaskPayload.builder()
                        .ner(NER.builder().nerFiles(NERFiles.builder().build()).build())
                        .verificationPerformed(false)
                        .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .accountId(1L)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(any()))
                .thenReturn(InstallationOperatorDetails.builder().build());

        // when
        service.submitToRegulator(requestTask, AppUser.builder().userId("user").build(), RequestActionType.NER_APPLICATION_SUBMITTED);

        // then
        assertNull(requestPayload.getVerificationReport());
        verify(nerValidationService, never()).validateVerificationReport(any());
    }
}
