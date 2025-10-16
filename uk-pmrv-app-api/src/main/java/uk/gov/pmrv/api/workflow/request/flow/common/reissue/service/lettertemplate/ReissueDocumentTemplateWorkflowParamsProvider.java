package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.lettertemplate;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class ReissueDocumentTemplateWorkflowParamsProvider 
	implements DocumentTemplateWorkflowParamsProvider<ReissueRequestPayload> {

	private final RequestService requestService;

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.REISSUE;
	}

	@Override
	public Map<String, Object> constructParams(ReissueRequestPayload payload, String requestId) {

		Request request = requestService.findRequestById(requestId);
		ReissueRequestMetadata metadata = (ReissueRequestMetadata) request.getMetadata();


        return Map.of(
        		"consolidationNumber", payload.getConsolidationNumber(),
					"changesDetails", metadata.getChangesDetails()
        		);
	}
}
