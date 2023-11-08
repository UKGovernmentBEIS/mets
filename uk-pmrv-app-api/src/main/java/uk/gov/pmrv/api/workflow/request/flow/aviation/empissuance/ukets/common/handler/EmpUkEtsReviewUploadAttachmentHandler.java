package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpUkEtsReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpUkEtsReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final EmpUkEtsReviewUploadAttachmentService empUkEtsReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        empUkEtsReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
