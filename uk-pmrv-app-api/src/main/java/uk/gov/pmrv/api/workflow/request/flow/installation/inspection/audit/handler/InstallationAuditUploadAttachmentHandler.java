package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionUploadAttachmentHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionUploadAttachmentService;

@Component
public class InstallationAuditUploadAttachmentHandler extends InstallationInspectionUploadAttachmentHandler {

    public InstallationAuditUploadAttachmentHandler(InstallationInspectionUploadAttachmentService attachmentService) {
        super(attachmentService);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.INSTALLATION_AUDIT_UPLOAD_ATTACHMENT;
    }
}
