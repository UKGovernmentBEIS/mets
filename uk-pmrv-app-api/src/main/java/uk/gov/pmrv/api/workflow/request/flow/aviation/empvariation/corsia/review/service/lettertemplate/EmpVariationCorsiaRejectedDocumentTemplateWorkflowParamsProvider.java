package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.lettertemplate;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
public class EmpVariationCorsiaRejectedDocumentTemplateWorkflowParamsProvider
	implements DocumentTemplateWorkflowParamsProvider<EmpVariationCorsiaRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REJECTED;
	}

	@Override
	public Map<String, Object> constructParams(EmpVariationCorsiaRequestPayload payload, String requestId) {
		final String rejectionReason = payload.getDetermination().getReason();
		
        return Map.of(
        		"rejectionReason", rejectionReason
        		);
	}
}
