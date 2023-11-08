package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class AirUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AirUploadAttachmentService service;

    @Override
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        service.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AIR_UPLOAD_ATTACHMENT;
    }
}
