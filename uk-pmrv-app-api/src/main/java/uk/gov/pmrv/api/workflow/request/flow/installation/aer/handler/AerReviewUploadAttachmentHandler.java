package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class AerReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AerReviewUploadAttachmentService aerReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        aerReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
