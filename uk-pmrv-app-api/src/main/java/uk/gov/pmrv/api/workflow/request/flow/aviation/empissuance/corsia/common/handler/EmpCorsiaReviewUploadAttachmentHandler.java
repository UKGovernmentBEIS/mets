package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service.EmpCorsiaReviewUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpCorsiaReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final EmpCorsiaReviewUploadAttachmentService empCorsiaReviewUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        empCorsiaReviewUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
