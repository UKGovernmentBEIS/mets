package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermitVariationUploadReviewGroupAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermitVariationReviewUploadAttachmentService permitVariationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        permitVariationUploadAttachmentService.uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMIT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
