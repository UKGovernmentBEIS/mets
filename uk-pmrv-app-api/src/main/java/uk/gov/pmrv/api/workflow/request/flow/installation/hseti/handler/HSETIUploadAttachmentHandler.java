package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISubmitService;

@Component
@RequiredArgsConstructor
public class HSETIUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final HSETISubmitService submitService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        submitService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.HSE_TI_UPLOAD_ATTACHMENT;
    }
}
