package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionUploadAttachmentHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionUploadAttachmentService;

@Component
public class InstallationOnsiteInspectionUploadAttachmentHandler extends InstallationInspectionUploadAttachmentHandler {

    public InstallationOnsiteInspectionUploadAttachmentHandler(InstallationInspectionUploadAttachmentService attachmentService) {
        super(attachmentService);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_UPLOAD_ATTACHMENT;
    }
}
