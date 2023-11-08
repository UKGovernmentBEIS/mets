package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.service.AviationDreUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class AviationDreUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AviationDreUploadAttachmentService aviationDreUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        aviationDreUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AVIATION_DRE_UPLOAD_ATTACHMENT;
    }
}
