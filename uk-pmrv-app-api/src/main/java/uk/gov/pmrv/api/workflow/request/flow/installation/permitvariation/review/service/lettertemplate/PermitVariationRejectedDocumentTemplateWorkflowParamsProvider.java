package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.lettertemplate;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;

import java.util.Map;

@Component
public class PermitVariationRejectedDocumentTemplateWorkflowParamsProvider
		implements DocumentTemplateWorkflowParamsProvider<PermitVariationRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REJECTED;
	}

	@Override
	public Map<String, Object> constructParams(PermitVariationRequestPayload payload, String requestId) {
		final PermitVariationRejectDetermination determination = (PermitVariationRejectDetermination) payload.getDetermination();
		
        return Map.of(
        		"officialNotice", determination.getOfficialNotice()
        		);
	}

}
