package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.lettertemplate;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class EmpVariationUkEtsRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider
		implements DocumentTemplateWorkflowParamsProvider<EmpVariationUkEtsRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.EMP_VARIATION_REGULATOR_LED_APPROVED;
	}

	@Override
	public Map<String, Object> constructParams(EmpVariationUkEtsRequestPayload payload, String requestId) {
		final TreeMap<EmpUkEtsReviewGroup, EmpAcceptedVariationDecisionDetails> sortedDecisions = new TreeMap<>(
				payload.getReviewGroupDecisionsRegulatorLed()); // TODO: consider changing the root property in payloads from EnumMap to TreeMap
		final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
				.values()
				.stream()
				.map(EmpAcceptedVariationDecisionDetails::getVariationScheduleItems)
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
        final Map<String, Object> params = new HashMap<>();
        params.put("consolidationNumber", payload.getEmpConsolidationNumber());
        params.put("variationScheduleItems", reviewGroupsVariationScheduleItems);
        params.put("reason", payload.getReasonRegulatorLed());
        return params;
	}
	
}
