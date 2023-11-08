package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaUploadReviewGroupAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {
	
	private final EmpVariationCorsiaReviewUploadAttachmentService empVariationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	empVariationUploadAttachmentService.uploadReviewGroupDecisionAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_VARIATION_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
