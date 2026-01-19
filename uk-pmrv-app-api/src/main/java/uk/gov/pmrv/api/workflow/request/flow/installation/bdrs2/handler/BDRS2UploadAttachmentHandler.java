package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2UploadAttachmentService;

@Component
@RequiredArgsConstructor
public class BDRS2UploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final BDRS2UploadAttachmentService bdrs2UploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        bdrs2UploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.BDRS2_UPLOAD_ATTACHMENT;
    }
}
