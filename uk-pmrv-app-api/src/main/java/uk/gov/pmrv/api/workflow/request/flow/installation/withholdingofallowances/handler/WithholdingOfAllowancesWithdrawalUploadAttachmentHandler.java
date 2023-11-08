package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawalUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawalUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final WithholdingOfAllowancesWithdrawalUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_UPLOAD_ATTACHMENT;
    }
}
