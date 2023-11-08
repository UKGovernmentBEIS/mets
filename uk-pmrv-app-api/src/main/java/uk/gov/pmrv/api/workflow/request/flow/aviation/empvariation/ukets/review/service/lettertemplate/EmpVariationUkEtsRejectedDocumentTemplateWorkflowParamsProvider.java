package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.lettertemplate;

import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
public class EmpVariationUkEtsRejectedDocumentTemplateWorkflowParamsProvider 
	implements DocumentTemplateWorkflowParamsProvider<EmpVariationUkEtsRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.EMP_VARIATION_REJECTED;
	}

	@Override
	public Map<String, Object> constructParams(EmpVariationUkEtsRequestPayload payload, String requestId) {
		final String rejectionReason = payload.getDetermination().getReason();
		
        return Map.of(
        		"rejectionReason", rejectionReason
        		);
	}
}
