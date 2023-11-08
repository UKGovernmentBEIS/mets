package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.AviationAerCorsiaReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class AviationAerCorsiaReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AviationAerCorsiaReviewUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AVIATION_AER_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
