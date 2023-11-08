package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.lettertemplate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;


@Component
public class EmpVariationUkEtsApprovedDocumentTemplateWorkflowParamsProvider 
	implements DocumentTemplateWorkflowParamsProvider<EmpVariationUkEtsRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.EMP_VARIATION_ACCEPTED;
	}

	@Override
	public Map<String, Object> constructParams(EmpVariationUkEtsRequestPayload payload, String requestId) {
		final EmpAcceptedVariationDecisionDetails variationDecisionDetails = (EmpAcceptedVariationDecisionDetails) payload
				.getEmpVariationDetailsReviewDecision().getDetails();
		
		final TreeMap<EmpUkEtsReviewGroup, EmpVariationReviewDecision> sortedDecisions = new TreeMap<>(
				payload.getReviewGroupDecisions());
		final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().getType() == EmpVariationReviewDecisionType.ACCEPTED)
				.map(entry -> entry.getValue().getDetails())
				.map(EmpAcceptedVariationDecisionDetails.class::cast)
				.map(EmpAcceptedVariationDecisionDetails::getVariationScheduleItems)
				.flatMap(List::stream)
				.toList();
		
        return Map.of(
        		"empConsolidationNumber", payload.getEmpConsolidationNumber(),
				"variationScheduleItems", Stream.concat(variationDecisionDetails.getVariationScheduleItems().stream(),
						reviewGroupsVariationScheduleItems.stream())
				.toList()
        		);
	}
}
