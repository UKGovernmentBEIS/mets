package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRRegulatorReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class ALRRegulatorReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final ALRRegulatorReviewUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
