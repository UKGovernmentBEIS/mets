package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERVerificationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class NERApplicationVerificationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final NERVerificationUploadAttachmentService nerVerificationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        nerVerificationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.NER_VERIFICATION_UPLOAD_ATTACHMENT;
    }
}
