package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsUploadReviewGroupAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {
	
	private final EmpVariationUkEtsReviewUploadAttachmentService empVariationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	empVariationUploadAttachmentService.uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_VARIATION_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
