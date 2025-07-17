package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionUploadAttachmentService;

@Component
@RequiredArgsConstructor
public abstract class InstallationInspectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final InstallationInspectionUploadAttachmentService attachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        attachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

}
