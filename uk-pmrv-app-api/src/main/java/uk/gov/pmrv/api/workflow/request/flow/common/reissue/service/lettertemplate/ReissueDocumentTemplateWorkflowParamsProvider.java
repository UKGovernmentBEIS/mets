package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.lettertemplate;

import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
public class ReissueDocumentTemplateWorkflowParamsProvider 
	implements DocumentTemplateWorkflowParamsProvider<ReissueRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.REISSUE;
	}

	@Override
	public Map<String, Object> constructParams(ReissueRequestPayload payload, String requestId) {
        return Map.of(
        		"consolidationNumber", payload.getConsolidationNumber()
        		);
	}
}
