package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionOperatorRespondUploadAttachmentService;

@RequiredArgsConstructor
public abstract class InstallationInspectionOperatorRespondUploadAttachmentHandler
    extends RequestTaskUploadAttachmentActionHandler {

    private final InstallationInspectionOperatorRespondUploadAttachmentService attachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        attachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }
}
