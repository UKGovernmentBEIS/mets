package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class PermanentCessationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final PermanentCessationUploadAttachmentService attachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        attachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.PERMANENT_CESSATION_UPLOAD;
    }
}
