package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRVerificationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class ALRApplicationVerificationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final ALRVerificationUploadAttachmentService alrVerificationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        alrVerificationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.ALR_VERIFICATION_UPLOAD_ATTACHMENT;
    }
}
