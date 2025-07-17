package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<InstallationAuditRequestPayload> {

    private final InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider commonParamsProvider;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.INSTALLATION_AUDIT_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(InstallationAuditRequestPayload payload, String requestId) {

        Map<String,Object> params =
                new HashMap<>(commonParamsProvider.constructParams(payload.getInstallationInspection()));

        params.put("inspectionDate", payload.getAuditYear().toString() + " Audit Report");

        return params;
    }


    public Map<String, Object> constructParamsFromAuditSubmitRequestTaskPayload(
            InstallationInspectionApplicationSubmitRequestTaskPayload payload,
            InstallationAuditRequestPayload requestPayload,
            String requestId) {

        Map<String,Object> params =
                new HashMap<>(commonParamsProvider.constructParams(payload.getInstallationInspection()));

        params.put("inspectionDate", requestPayload.getAuditYear().toString() + " Audit Report");

        return params;
    }
}
