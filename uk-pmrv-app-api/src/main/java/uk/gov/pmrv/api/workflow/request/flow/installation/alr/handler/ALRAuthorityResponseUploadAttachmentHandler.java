package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class ALRAuthorityResponseUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final ALRAuthorityResponseUploadAttachmentService alrAuthorityResponseUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        alrAuthorityResponseUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.ALR_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT;
    }
}
