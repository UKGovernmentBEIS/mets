package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.lettertemplate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

@Component
public class PermitVariationGrantedDocumentTemplateWorkflowParamsProvider
		implements DocumentTemplateWorkflowParamsProvider<PermitVariationRequestPayload> {

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_GRANTED;
	}

	@Override
	public Map<String, Object> constructParams(PermitVariationRequestPayload payload, String requestId) {
		final PermitVariationGrantDetermination determination = (PermitVariationGrantDetermination) payload.getDetermination();
		final PermitAcceptedVariationDecisionDetails variationDecisionDetails = (PermitAcceptedVariationDecisionDetails) payload
				.getPermitVariationDetailsReviewDecision().getDetails();
		
		final TreeMap<PermitReviewGroup, PermitVariationReviewDecision> sortedDecisions = new TreeMap<>(
				payload.getReviewGroupDecisions()); // TODO: consider changing the root property in payloads from EnumMap to TreeMap
		final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue().getType() == ReviewDecisionType.ACCEPTED)
				.map(entry -> entry.getValue().getDetails())
				.map(PermitAcceptedVariationDecisionDetails.class::cast)
				.map(decision -> decision.getVariationScheduleItems())
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
        return Map.of(
        		"permitConsolidationNumber", payload.getPermitConsolidationNumber(),
        		"activationDate", determination.getActivationDate(),
				"variationScheduleItems", Stream.concat(variationDecisionDetails.getVariationScheduleItems().stream(),
						reviewGroupsVariationScheduleItems.stream())
                .collect(Collectors.toList())
        		);
	}

}
