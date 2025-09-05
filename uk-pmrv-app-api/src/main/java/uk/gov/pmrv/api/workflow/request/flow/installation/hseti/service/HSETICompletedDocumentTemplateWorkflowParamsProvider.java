package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HSETICompletedDocumentTemplateWorkflowParamsProvider
        implements DocumentTemplateWorkflowParamsProvider<HSETIRequestPayload> {

    @Override
    public Map<String, Object> constructParams(HSETIRequestPayload payload, String requestId) {

        Map<String,Object> params = new HashMap<>();

        params.put("overallDecision", payload.getOverallDecision());

        return params;
    }

    public Map<String, Object> constructParams(HSETIApplicationRegulatorReviewSubmitRequestTaskPayload payload) {

        Map<String,Object> params = new HashMap<>();

        params.put("overallDecision", payload.getOverallDecision());

        return params;
    }


    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED;
    }
}
