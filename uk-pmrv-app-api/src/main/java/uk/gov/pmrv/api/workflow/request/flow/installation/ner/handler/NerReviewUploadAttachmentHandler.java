package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class NerReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final NerReviewUploadAttachmentService nerReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        nerReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.NER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
