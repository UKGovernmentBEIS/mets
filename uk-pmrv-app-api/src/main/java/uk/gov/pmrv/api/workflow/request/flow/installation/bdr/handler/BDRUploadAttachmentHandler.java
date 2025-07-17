package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class BDRUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final BDRUploadAttachmentService attachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        attachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.BDR_UPLOAD_ATTACHMENT;
    }
}
