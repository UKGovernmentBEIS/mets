package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


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
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.reporting.service.bdr.BaselineDataReportFreeAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRCompleteServiceTest {

    @InjectMocks
    private BDRCompleteService bdrCompleteService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private AccountFileAttachmentService accountFileAttachmentService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Mock
    BaselineDataReportFreeAllocationService baselineDataReportFreeAllocationService;

    @Test
    public void complete() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final String requestId = "BDR00001-2025";
        final Long accountId = 1L;
        final UUID attachmentId = UUID.randomUUID();
        final UUID attachmentId1 = UUID.randomUUID();
        final UUID bdrFileUuid = UUID.randomUUID();

        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                .bdrAttachments(Map.of(attachmentId, "test"))
                .regulatorReviewer("test")
                .regulatorReviewAttachments(Map.of(attachmentId1, "test"))
                .regulatorReviewOutcome(BDRApplicationRegulatorReviewOutcome.builder().bdrFile(bdrFileUuid).build())
                .bdr(BDR.builder().isApplicationForFreeAllocation(true).build())
                .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .competentAuthority(competentAuthority)
                .type(RequestType.BDR)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        // Act
        bdrCompleteService.complete(requestId);

        // Verify 1: The easy one
        verify(baselineDataReportFreeAllocationService, times(1)).createFreeAllocationEntry(accountId, true);

        // Verify 2: Use ArgumentCaptor to solve the "Comparison Failure"
        ArgumentCaptor<BDRApplicationCompletedRequestActionPayload> actionPayloadCaptor =
                ArgumentCaptor.forClass(BDRApplicationCompletedRequestActionPayload.class);

        verify(requestService).addActionToRequest(
                eq(request),
                actionPayloadCaptor.capture(),
                eq(RequestActionType.BDR_APPLICATION_COMPLETED),
                eq("test")
        );

        verify(accountFileAttachmentService, times(1)).updateOrInsertAccountFileAttachment(
            argThat(dto ->
                dto.getWorkflow() == AccountFileAttachmentWorkflow.BDR &&
                    dto.getWorkflowSubtype() == AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT &&
                    dto.getOriginatedRequestId().equals(requestId) &&
                    dto.getStatus() == AccountFileAttachmentStatus.FINALIZED &&
                    dto.getAccountId().equals(accountId) &&
                    dto.getPeriod().equals("2026-2030") &&
                    dto.getFileUuid().equals(bdrFileUuid.toString()) &&
                    dto.getCompetentAuthority().equals(competentAuthority)
            )
        );

        // Verify the captured payload fields actually match what you expect
        BDRApplicationCompletedRequestActionPayload captured = actionPayloadCaptor.getValue();
        assertEquals(installationOperatorDetails, captured.getInstallationOperatorDetails());
        assertEquals(requestPayload.getBdrAttachments(), captured.getBdrAttachments());
        assertEquals(requestPayload.getRegulatorReviewAttachments(), captured.getRegulatorReviewAttachments());
    }

}
