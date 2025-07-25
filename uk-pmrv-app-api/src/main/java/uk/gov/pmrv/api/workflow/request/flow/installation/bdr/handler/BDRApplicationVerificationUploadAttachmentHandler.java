package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRVerificationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class BDRApplicationVerificationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final BDRVerificationUploadAttachmentService bdrVerificationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        bdrVerificationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.BDR_VERIFICATION_UPLOAD_ATTACHMENT;
    }
}
