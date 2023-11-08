package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class NonComplianceUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final NonComplianceUploadAttachmentService uploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT;
    }
}
