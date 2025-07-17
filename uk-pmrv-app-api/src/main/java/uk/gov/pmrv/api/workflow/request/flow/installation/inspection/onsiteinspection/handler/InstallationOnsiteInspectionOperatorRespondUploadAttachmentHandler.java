package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionOperatorRespondUploadAttachmentHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionOperatorRespondUploadAttachmentService;

@Component
public class InstallationOnsiteInspectionOperatorRespondUploadAttachmentHandler
    extends InstallationInspectionOperatorRespondUploadAttachmentHandler {

    public InstallationOnsiteInspectionOperatorRespondUploadAttachmentHandler(
            InstallationInspectionOperatorRespondUploadAttachmentService attachmentService) {
        super(attachmentService);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_UPLOAD_ATTACHMENT;
    }
}
