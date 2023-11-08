package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.AviationAerUkEtsReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class AviationAerUkEtsReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AviationAerUkEtsReviewUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AVIATION_AER_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
