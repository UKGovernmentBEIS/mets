package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class DoalAuthorityResponseUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final DoalAuthorityResponseUploadAttachmentService doalAuthorityResponseUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        doalAuthorityResponseUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.DOAL_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT;
    }
}
