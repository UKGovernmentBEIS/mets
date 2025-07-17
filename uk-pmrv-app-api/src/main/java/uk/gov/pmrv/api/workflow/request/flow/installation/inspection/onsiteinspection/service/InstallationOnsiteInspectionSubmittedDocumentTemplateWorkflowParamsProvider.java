package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider
        implements DocumentTemplateWorkflowParamsProvider<InstallationOnsiteInspectionRequestPayload> {

    private final InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider commonParamsProvider;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(InstallationOnsiteInspectionRequestPayload payload, String requestId) {

        Map<String,Object> params =
                new HashMap<>(commonParamsProvider.constructParams(payload.getInstallationInspection()));

        params.put("inspectionDate", payload.getInstallationInspection().getDetails().getDate().format(InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider.FORMATTER));

        return params;
    }


    public Map<String, Object> constructParamsFromOnsiteInspectionSubmitRequestTaskPayload(
            InstallationInspectionApplicationSubmitRequestTaskPayload payload,
            InstallationOnsiteInspectionRequestPayload requestPayload,
            String requestId) {

        Map<String,Object> params =
                new HashMap<>(commonParamsProvider.constructParams(payload.getInstallationInspection()));

        params.put("inspectionDate", (payload.getInstallationInspection().getDetails().getDate().format(InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider.FORMATTER)));

        return params;
    }
}
