package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerVerificationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class AerVerificationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AerVerificationUploadAttachmentService aerVerificationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        aerVerificationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AER_VERIFICATION_UPLOAD_ATTACHMENT;
    }
}
