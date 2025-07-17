package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionOperatorRespondUploadAttachmentHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionOperatorRespondUploadAttachmentService;

@Component
public class InstallationAuditOperatorRespondUploadAttachmentHandler
    extends InstallationInspectionOperatorRespondUploadAttachmentHandler {

    public InstallationAuditOperatorRespondUploadAttachmentHandler(
            InstallationInspectionOperatorRespondUploadAttachmentService attachmentService) {
        super(attachmentService);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_UPLOAD_ATTACHMENT;
    }
}
