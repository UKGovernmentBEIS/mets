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
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
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
    private BaselineDataReportFreeAllocationService baselineDataReportFreeAllocationService;

    @Test
    void complete() {
        // Arrange
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final String requestId = "BDR00001-2025";
        final Long accountId = 1L;
        final UUID bdrFileUuid = UUID.randomUUID();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                .regulatorReviewer("test")
                .regulatorReviewOutcome(
                        BDRApplicationRegulatorReviewOutcome.builder()
                                .bdrFile(bdrFileUuid)
                                .build()
                )
                .bdr(BDR.builder()
                        .isApplicationForFreeAllocation(true)
                        .build())
                .build();

        Request request = Request.builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .competentAuthority(competentAuthority)
                .type(RequestType.BDR)
                .build();

        // IMPORTANT: complete() calls findRequestById twice
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Act
        bdrCompleteService.complete(requestId);

        // ✅ Verify free allocation entry
        verify(baselineDataReportFreeAllocationService, times(1))
                .createFreeAllocationEntry(accountId, true);

        // ✅ Capture AccountFileAttachmentDTO
        ArgumentCaptor<AccountFileAttachmentDTO> captor =
                ArgumentCaptor.forClass(AccountFileAttachmentDTO.class);

        verify(accountFileAttachmentService, times(1))
                .updateOrInsertAccountFileAttachment(captor.capture());

        AccountFileAttachmentDTO dto = captor.getValue();



        // Optional: verify request was fetched twice
        verify(requestService, times(2)).findRequestById(requestId);
    }

}
