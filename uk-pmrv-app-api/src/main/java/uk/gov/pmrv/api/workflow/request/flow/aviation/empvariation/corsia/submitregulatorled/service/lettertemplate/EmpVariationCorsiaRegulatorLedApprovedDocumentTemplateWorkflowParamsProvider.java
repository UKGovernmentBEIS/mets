package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.lettertemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
public class EmpVariationCorsiaRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider
		implements DocumentTemplateWorkflowParamsProvider<EmpVariationCorsiaRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED;
	}

	@Override
	public Map<String, Object> constructParams(final EmpVariationCorsiaRequestPayload payload, final String requestId) {
		
		final TreeMap<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> sortedDecisions = 
			new TreeMap<>(payload.getReviewGroupDecisionsRegulatorLed());
		final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
				.values()
				.stream()
				.map(EmpAcceptedVariationDecisionDetails::getVariationScheduleItems)
				.flatMap(List::stream)
				.toList();
		
        final Map<String, Object> params = new HashMap<>();
        params.put("consolidationNumber", payload.getEmpConsolidationNumber());
        params.put("variationScheduleItems", reviewGroupsVariationScheduleItems);
        params.put("reason", payload.getReasonRegulatorLed());
		
        return params;
	}
	
}
