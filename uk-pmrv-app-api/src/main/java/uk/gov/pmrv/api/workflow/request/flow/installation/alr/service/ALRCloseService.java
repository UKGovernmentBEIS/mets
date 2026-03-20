package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ALRCloseService {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final AccountFileAttachmentService accountFileAttachmentService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Transactional
    public void addClosedRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        Optional.ofNullable(requestPayload.getRegulatorReviewOutcome())
                .map(outcome -> outcome.getDetermination())
                .map(det -> ((ALRClosedDetermination) det).getAlrFile())
                .ifPresentOrElse(
                        file -> updateOrInsertAccountFileAttachment(request, requestPayload, AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT, file.toString()),
                        () -> accountFileAttachmentService.updateAccountFileAttachmentsStatusByAccountId(AccountFileAttachmentWorkflow.ALR,
                                AccountFileAttachmentStatus.FINALIZED, request.getAccountId())
                );

        if (requestPayload.getVerificationReport() != null && requestPayload.getVerificationReport().getVerificationData().getOpinionStatement().getOpinionStatementFile() != null) {
            updateOrInsertAccountFileAttachment(request, requestPayload, AccountFileAttachmentWorkflowSubType.ALR_VOS,
                    requestPayload.getVerificationReport().getVerificationData().getOpinionStatement().getOpinionStatementFile().toString());
        }

        ALRApplicationClosedRequestActionPayload actionPayload = ALR_MAPPER
                .toALRApplicationClosedRequestActionPayload(requestPayload);

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.ALR_APPLICATION_CLOSED,
                requestPayload.getRegulatorAssignee());
    }

    private void updateOrInsertAccountFileAttachment(Request request, ALRRequestPayload requestPayload,
                                                     AccountFileAttachmentWorkflowSubType subType, String file) {
        AccountFileAttachmentDTO accountFileAttachmentDTO = AccountFileAttachmentDTO.builder()
                .workflow(AccountFileAttachmentWorkflow.ALR)
                .workflowSubtype(subType)
                .originatedRequestId(request.getId())
                .status(AccountFileAttachmentStatus.FINALIZED)
                .accountId(request.getAccountId())
                .period(requestPayload.getReportingYear().toString())
                .fileUuid(file)
                .competentAuthority(request.getCompetentAuthority())
                .build();

        accountFileAttachmentService.updateOrInsertAccountFileAttachment(accountFileAttachmentDTO);
    }
}
