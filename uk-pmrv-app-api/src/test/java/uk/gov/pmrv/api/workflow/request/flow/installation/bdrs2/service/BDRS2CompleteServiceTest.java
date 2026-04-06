package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Files;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BDRS2CompleteServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @InjectMocks
    private BDRS2CompleteService service;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Test
    void complete_shouldInsertAccountFileAttachment_whenFileExists() {
        // Arrange
        String requestId = "REQ-1";
        UUID fileUuid = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2Files files = mock(BDRS2Files.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getBdrs2Files()).thenReturn(files);
        when(files.getFile()).thenReturn(fileUuid);

        when(request.getAccountId()).thenReturn(100L);
        when(request.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);

        // Act
        service.complete(requestId);

        // Assert
        ArgumentCaptor<AccountFileAttachmentDTO> captor =
                ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);

        verify(accountFileAttachmentService)
                .updateOrInsertAccountFileAttachment(captor.capture());

        AccountFileAttachmentDTO dto = captor.getValue();

        assertEquals(AccountFileAttachmentWorkflow.BDRS2, dto.getWorkflow());
        assertEquals(AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT, dto.getWorkflowSubtype());
        assertEquals(requestId, dto.getOriginatedRequestId());
        assertEquals(AccountFileAttachmentStatus.FINALIZED, dto.getStatus());
        assertEquals(100L, dto.getAccountId());
        assertEquals("2026-2030", dto.getPeriod());
        assertEquals(fileUuid.toString(), dto.getFileUuid());
        assertEquals(CompetentAuthorityEnum.ENGLAND, dto.getCompetentAuthority());
    }

    @Test
    void complete_shouldInsertMmpFileAttachment_whenMmpFileExists() {
        String requestId = "REQ-1";
        UUID bdrFileUuid = UUID.randomUUID();
        UUID mmpFileUuid = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2Files bdrFiles = mock(BDRS2Files.class);
        BDRS2Files mmpFiles = mock(BDRS2Files.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getBdrs2Files()).thenReturn(bdrFiles);
        when(bdrFiles.getFile()).thenReturn(bdrFileUuid);
        when(bdrs2.getMmpFiles()).thenReturn(mmpFiles);
        when(mmpFiles.getFile()).thenReturn(mmpFileUuid);
        when(request.getAccountId()).thenReturn(100L);
        when(request.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);

        service.complete(requestId);

        ArgumentCaptor<AccountFileAttachmentDTO> captor = ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);
        verify(accountFileAttachmentService, times(2)).updateOrInsertAccountFileAttachment(captor.capture());

        List<AccountFileAttachmentDTO> capturedDtos = captor.getAllValues();
        AccountFileAttachmentDTO mmpDto = capturedDtos.get(1);
        assertEquals(AccountFileAttachmentWorkflow.BDRS2, mmpDto.getWorkflow());
        assertEquals(AccountFileAttachmentWorkflowSubType.BDRS2_MMP, mmpDto.getWorkflowSubtype());
        assertEquals(requestId, mmpDto.getOriginatedRequestId());
        assertEquals(AccountFileAttachmentStatus.FINALIZED, mmpDto.getStatus());
        assertEquals(100L, mmpDto.getAccountId());
        assertEquals("2026-2030", mmpDto.getPeriod());
        assertEquals(mmpFileUuid.toString(), mmpDto.getFileUuid());
        assertEquals(CompetentAuthorityEnum.ENGLAND, mmpDto.getCompetentAuthority());
    }

    @Test
    void complete_shouldInsertVosFileAttachments_whenVosFilesExist() {
        String requestId = "REQ-1";
        UUID bdrFileUuid = UUID.randomUUID();
        UUID vosUuid1 = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2Files bdrFiles = mock(BDRS2Files.class);
        BDRS2VerificationReport verificationReport = mock(BDRS2VerificationReport.class);
        BDRS2VerificationData verificationData = mock(BDRS2VerificationData.class);
        BDRS2VerificationOpinionStatement opinionStatement = mock(BDRS2VerificationOpinionStatement.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getBdrs2Files()).thenReturn(bdrFiles);
        when(bdrFiles.getFile()).thenReturn(bdrFileUuid);
        when(bdrs2.getMmpFiles()).thenReturn(null);
        when(payload.getVerificationReport()).thenReturn(verificationReport);
        when(verificationReport.getVerificationData()).thenReturn(verificationData);
        when(verificationData.getOpinionStatement()).thenReturn(opinionStatement);
        when(opinionStatement.getOpinionStatementFile()).thenReturn(vosUuid1);
        when(request.getAccountId()).thenReturn(100L);
        when(request.getCompetentAuthority()).thenReturn(CompetentAuthorityEnum.ENGLAND);

        service.complete(requestId);

        ArgumentCaptor<AccountFileAttachmentDTO> captor = ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);
        verify(accountFileAttachmentService, times(2)).updateOrInsertAccountFileAttachment(captor.capture());

        List<AccountFileAttachmentDTO> allAttachments = captor.getAllValues();

        // First call should be BDR_ATTACHMENT
        assertThat(allAttachments.get(0))
                .returns(AccountFileAttachmentWorkflow.BDRS2, AccountFileAttachmentDTO::getWorkflow)
                .returns(AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT, AccountFileAttachmentDTO::getWorkflowSubtype)
                .returns(AccountFileAttachmentStatus.FINALIZED, AccountFileAttachmentDTO::getStatus);

        // Second call should be BDRS2_VOS (main file only)
        assertThat(allAttachments.get(1))
                .returns(AccountFileAttachmentWorkflow.BDRS2, AccountFileAttachmentDTO::getWorkflow)
                .returns(AccountFileAttachmentWorkflowSubType.BDRS2_VOS, AccountFileAttachmentDTO::getWorkflowSubtype)
                .returns(requestId, AccountFileAttachmentDTO::getOriginatedRequestId)
                .returns(AccountFileAttachmentStatus.FINALIZED, AccountFileAttachmentDTO::getStatus)
                .returns(100L, AccountFileAttachmentDTO::getAccountId)
                .returns("2026-2030", AccountFileAttachmentDTO::getPeriod)
                .returns(vosUuid1.toString(), AccountFileAttachmentDTO::getFileUuid)
                .returns(CompetentAuthorityEnum.ENGLAND, AccountFileAttachmentDTO::getCompetentAuthority);
    }

    @Test
    void complete_shouldNotInsertAccountFileAttachment_whenFileIsNull() {
        // Arrange
        String requestId = "REQ-1";

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2Files files = mock(BDRS2Files.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getBdrs2Files()).thenReturn(files);
        when(files.getFile()).thenReturn(null);

        // Act
        service.complete(requestId);

        // Assert
        verify(accountFileAttachmentService, never())
                .updateOrInsertAccountFileAttachment(any());
    }

    @Test
    void addRequestAction_shouldAddCompletedActionToRequest() {
        // given
        String requestId = "REQ-1";
        Long accountId = 1L;
        Long verificationBodyId = 2L;

        Request request = mock(Request.class);
        BDRS2RequestPayload requestPayload = mock(BDRS2RequestPayload.class);
        BDRS2VerificationReport verificationReport = mock(BDRS2VerificationReport.class);
        InstallationOperatorDetails operatorDetails = mock(InstallationOperatorDetails.class);

        Map<UUID, String> bdrs2Attachments = new HashMap<>();
        Map<UUID, String> regulatorAttachments = new HashMap<>();
        String regulatorReviewer = "reviewer";

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(request.getAccountId()).thenReturn(accountId);
        when(request.getVerificationBodyId()).thenReturn(verificationBodyId);

        when(requestPayload.getVerificationReport()).thenReturn(verificationReport);
        when(requestPayload.getBdrs2Attachments()).thenReturn(bdrs2Attachments);
        when(requestPayload.getRegulatorReviewAttachments()).thenReturn(regulatorAttachments);
        when(requestPayload.getRegulatorReviewer()).thenReturn(regulatorReviewer);

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(operatorDetails);

        ArgumentCaptor<BDRS2ApplicationCompletedRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(BDRS2ApplicationCompletedRequestActionPayload.class);

        // when
        service.addRequestAction(requestId);

        // then
        verify(requestVerificationService).refreshVerificationReportVBDetails(
                verificationReport,
                verificationBodyId
        );

        verify(requestService).addActionToRequest(
                eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.BDRS2_APPLICATION_COMPLETED),
                eq(regulatorReviewer)
        );

        BDRS2ApplicationCompletedRequestActionPayload captured = payloadCaptor.getValue();

        assertEquals(bdrs2Attachments, captured.getBdrs2Attachments());
        assertEquals(regulatorAttachments, captured.getRegulatorReviewAttachments());
    }
}
