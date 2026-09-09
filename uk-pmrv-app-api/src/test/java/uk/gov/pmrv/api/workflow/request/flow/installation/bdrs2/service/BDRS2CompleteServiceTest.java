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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.*;

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

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @InjectMocks
    private BDRS2CompleteService service;

    @Test
    void complete_shouldUpdateAndFinalizeBdrAttachment_whenRegulatorReviewOutcomeFileExists() {
        // Arrange
        String requestId = "REQ-1";
        Long accountId = 100L;
        UUID fileUuid = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome =
                mock(BDRS2ApplicationRegulatorReviewOutcome.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(request.getAccountId()).thenReturn(accountId);
        when(request.getCompetentAuthority())
                .thenReturn(CompetentAuthorityEnum.ENGLAND);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getMmpFiles()).thenReturn(null);
        when(payload.getVerificationReport()).thenReturn(null);

        when(payload.getRegulatorReviewOutcome())
                .thenReturn(regulatorReviewOutcome);
        when(regulatorReviewOutcome.getFile())
                .thenReturn(fileUuid);

        // Act
        service.complete(requestId);

        // Assert
        ArgumentCaptor<AccountFileAttachmentDTO> captor =
                ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);

        verify(accountFileAttachmentService)
                .updateOrInsertAccountFileAttachment(captor.capture());

        AccountFileAttachmentDTO dto = captor.getValue();

        assertEquals(AccountFileAttachmentWorkflow.BDRS2, dto.getWorkflow());
        assertEquals(
                AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT,
                dto.getWorkflowSubtype()
        );
        assertEquals(requestId, dto.getOriginatedRequestId());
        assertEquals(AccountFileAttachmentStatus.FINALIZED, dto.getStatus());
        assertEquals(accountId, dto.getAccountId());
        assertEquals("2026-2030", dto.getPeriod());
        assertEquals(fileUuid.toString(), dto.getFileUuid());
        assertEquals(
                CompetentAuthorityEnum.ENGLAND,
                dto.getCompetentAuthority()
        );

        verify(accountFileAttachmentService, never())
                .updateAccountFileAttachmentStatus(
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void complete_shouldFinalizeExistingBdrAttachment_whenRegulatorReviewOutcomeFileIsNull() {
        // Arrange
        String requestId = "REQ-1";
        Long accountId = 100L;

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome =
                mock(BDRS2ApplicationRegulatorReviewOutcome.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(request.getAccountId()).thenReturn(accountId);

        when(payload.getRegulatorReviewOutcome())
                .thenReturn(regulatorReviewOutcome);
        when(regulatorReviewOutcome.getFile())
                .thenReturn(null);
        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getMmpFiles()).thenReturn(null);
        when(payload.getVerificationReport()).thenReturn(null);

        // Act
        service.complete(requestId);

        // Assert
        verify(accountFileAttachmentService)
                .updateAccountFileAttachmentStatus(
                        AccountFileAttachmentWorkflow.BDRS2,
                        AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT,
                        "2026-2030",
                        AccountFileAttachmentStatus.FINALIZED,
                        accountId
                );

        verify(accountFileAttachmentService, never())
                .updateOrInsertAccountFileAttachment(any());
    }

    @Test
    void complete_shouldInsertMmpFileAttachment_whenMmpFileExists() {
        // Arrange
        String requestId = "REQ-1";
        Long accountId = 100L;
        UUID bdrFileUuid = UUID.randomUUID();
        UUID mmpFileUuid = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome =
                mock(BDRS2ApplicationRegulatorReviewOutcome.class);
        BDRS2 bdrs2 = mock(BDRS2.class);
        BDRS2Files mmpFiles = mock(BDRS2Files.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(request.getAccountId()).thenReturn(accountId);
        when(request.getCompetentAuthority())
                .thenReturn(CompetentAuthorityEnum.ENGLAND);

        when(payload.getRegulatorReviewOutcome())
                .thenReturn(regulatorReviewOutcome);
        when(regulatorReviewOutcome.getFile())
                .thenReturn(bdrFileUuid);

        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getMmpFiles()).thenReturn(mmpFiles);
        when(mmpFiles.getFile()).thenReturn(mmpFileUuid);

        // Act
        service.complete(requestId);

        // Assert
        ArgumentCaptor<AccountFileAttachmentDTO> captor =
                ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);

        verify(accountFileAttachmentService, times(2))
                .updateOrInsertAccountFileAttachment(captor.capture());

        List<AccountFileAttachmentDTO> capturedDtos = captor.getAllValues();

        AccountFileAttachmentDTO bdrDto = capturedDtos.get(0);

        assertEquals(
                AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT,
                bdrDto.getWorkflowSubtype()
        );
        assertEquals(bdrFileUuid.toString(), bdrDto.getFileUuid());

        AccountFileAttachmentDTO mmpDto = capturedDtos.get(1);

        assertEquals(AccountFileAttachmentWorkflow.BDRS2, mmpDto.getWorkflow());
        assertEquals(
                AccountFileAttachmentWorkflowSubType.BDRS2_MMP,
                mmpDto.getWorkflowSubtype()
        );
        assertEquals(requestId, mmpDto.getOriginatedRequestId());
        assertEquals(AccountFileAttachmentStatus.FINALIZED, mmpDto.getStatus());
        assertEquals(accountId, mmpDto.getAccountId());
        assertEquals("2026-2030", mmpDto.getPeriod());
        assertEquals(mmpFileUuid.toString(), mmpDto.getFileUuid());
        assertEquals(
                CompetentAuthorityEnum.ENGLAND,
                mmpDto.getCompetentAuthority()
        );
    }

    @Test
    void complete_shouldInsertVosFileAttachments_whenVosFilesExist() {
        // Arrange
        String requestId = "REQ-1";
        Long accountId = 100L;
        UUID bdrFileUuid = UUID.randomUUID();
        UUID vosUuid = UUID.randomUUID();

        Request request = mock(Request.class);
        BDRS2RequestPayload payload = mock(BDRS2RequestPayload.class);
        BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome =
                mock(BDRS2ApplicationRegulatorReviewOutcome.class);
        BDRS2 bdrs2 = mock(BDRS2.class);

        BDRS2VerificationReport verificationReport =
                mock(BDRS2VerificationReport.class);
        BDRS2VerificationData verificationData =
                mock(BDRS2VerificationData.class);
        BDRS2VerificationOpinionStatement opinionStatement =
                mock(BDRS2VerificationOpinionStatement.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(payload);
        when(request.getAccountId()).thenReturn(accountId);
        when(request.getCompetentAuthority())
                .thenReturn(CompetentAuthorityEnum.ENGLAND);

        when(payload.getRegulatorReviewOutcome())
                .thenReturn(regulatorReviewOutcome);
        when(regulatorReviewOutcome.getFile())
                .thenReturn(bdrFileUuid);

        when(payload.getBdrs2()).thenReturn(bdrs2);
        when(bdrs2.getMmpFiles()).thenReturn(null);

        when(payload.getVerificationReport())
                .thenReturn(verificationReport);
        when(verificationReport.getVerificationData())
                .thenReturn(verificationData);
        when(verificationData.getOpinionStatement())
                .thenReturn(opinionStatement);
        when(opinionStatement.getOpinionStatementFile())
                .thenReturn(vosUuid);

        // Act
        service.complete(requestId);

        // Assert
        ArgumentCaptor<AccountFileAttachmentDTO> captor =
                ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);

        verify(accountFileAttachmentService, times(2))
                .updateOrInsertAccountFileAttachment(captor.capture());

        List<AccountFileAttachmentDTO> allAttachments = captor.getAllValues();

        // First call: regulator-reviewed BDR attachment
        assertThat(allAttachments.get(0))
                .returns(
                        AccountFileAttachmentWorkflow.BDRS2,
                        AccountFileAttachmentDTO::getWorkflow
                )
                .returns(
                        AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT,
                        AccountFileAttachmentDTO::getWorkflowSubtype
                )
                .returns(
                        AccountFileAttachmentStatus.FINALIZED,
                        AccountFileAttachmentDTO::getStatus
                )
                .returns(
                        bdrFileUuid.toString(),
                        AccountFileAttachmentDTO::getFileUuid
                );

        // Second call: VOS
        assertThat(allAttachments.get(1))
                .returns(
                        AccountFileAttachmentWorkflow.BDRS2,
                        AccountFileAttachmentDTO::getWorkflow
                )
                .returns(
                        AccountFileAttachmentWorkflowSubType.BDRS2_VOS,
                        AccountFileAttachmentDTO::getWorkflowSubtype
                )
                .returns(
                        requestId,
                        AccountFileAttachmentDTO::getOriginatedRequestId
                )
                .returns(
                        AccountFileAttachmentStatus.FINALIZED,
                        AccountFileAttachmentDTO::getStatus
                )
                .returns(
                        accountId,
                        AccountFileAttachmentDTO::getAccountId
                )
                .returns(
                        "2026-2030",
                        AccountFileAttachmentDTO::getPeriod
                )
                .returns(
                        vosUuid.toString(),
                        AccountFileAttachmentDTO::getFileUuid
                )
                .returns(
                        CompetentAuthorityEnum.ENGLAND,
                        AccountFileAttachmentDTO::getCompetentAuthority
                );
    }

    @Test
    void addRequestAction_shouldAddCompletedActionToRequest() {
        // given
        String requestId = "REQ-1";
        Long accountId = 1L;
        Long verificationBodyId = 2L;

        Request request = mock(Request.class);
        BDRS2RequestPayload requestPayload =
                mock(BDRS2RequestPayload.class);
        BDRS2VerificationReport verificationReport =
                mock(BDRS2VerificationReport.class);
        InstallationOperatorDetails operatorDetails =
                mock(InstallationOperatorDetails.class);

        Map<UUID, String> bdrs2Attachments = new HashMap<>();
        Map<UUID, String> regulatorAttachments = new HashMap<>();
        String regulatorReviewer = "reviewer";

        when(requestService.findRequestById(requestId))
                .thenReturn(request);
        when(request.getPayload())
                .thenReturn(requestPayload);
        when(request.getAccountId())
                .thenReturn(accountId);
        when(request.getVerificationBodyId())
                .thenReturn(verificationBodyId);

        when(requestPayload.getVerificationReport())
                .thenReturn(verificationReport);
        when(requestPayload.getBdrs2Attachments())
                .thenReturn(bdrs2Attachments);
        when(requestPayload.getRegulatorReviewAttachments())
                .thenReturn(regulatorAttachments);
        when(requestPayload.getRegulatorReviewer())
                .thenReturn(regulatorReviewer);

        when(installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(accountId))
                .thenReturn(operatorDetails);

        ArgumentCaptor<BDRS2ApplicationCompletedRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(
                        BDRS2ApplicationCompletedRequestActionPayload.class
                );

        // when
        service.addRequestAction(requestId);

        // then
        verify(requestVerificationService)
                .refreshVerificationReportVBDetails(
                        verificationReport,
                        verificationBodyId
                );

        verify(requestService).addActionToRequest(
                eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.BDRS2_APPLICATION_COMPLETED),
                eq(regulatorReviewer)
        );

        BDRS2ApplicationCompletedRequestActionPayload captured =
                payloadCaptor.getValue();

        assertEquals(
                bdrs2Attachments,
                captured.getBdrs2Attachments()
        );
        assertEquals(
                regulatorAttachments,
                captured.getRegulatorReviewAttachments()
        );
    }
}
