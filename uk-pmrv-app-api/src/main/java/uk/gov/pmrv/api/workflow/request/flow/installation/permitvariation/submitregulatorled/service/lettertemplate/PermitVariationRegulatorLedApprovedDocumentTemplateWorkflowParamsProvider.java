package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.lettertemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

@Component
@RequiredArgsConstructor
public class PermitVariationRegulatorLedApprovedDocumentTemplateWorkflowParamsProvider
		implements DocumentTemplateWorkflowParamsProvider<PermitVariationRequestPayload> {
	
	private final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

	@Override
	public DocumentTemplateGenerationContextActionType getContextActionType() {
		return DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REGULATOR_LED_APPROVED;
	}

	@Override
	public Map<String, Object> constructParams(PermitVariationRequestPayload payload, String requestId) {
		final PermitVariationRegulatorLedGrantDetermination determination = payload.getDeterminationRegulatorLed();
		final PermitAcceptedVariationDecisionDetails variationDetailsDecision = payload
				.getPermitVariationDetailsReviewDecisionRegulatorLed();
		
		final TreeMap<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> sortedDecisions = new TreeMap<>(
				payload.getReviewGroupDecisionsRegulatorLed()); // TODO: consider changing the root property in payloads from EnumMap to TreeMap
		final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
				.entrySet()
				.stream()
				.map(decision -> decision.getValue().getVariationScheduleItems())
				.flatMap(List::stream)
				.collect(Collectors.toList());
		
        final Map<String, Object> params = new HashMap<>();
        params.put("permitConsolidationNumber", payload.getPermitConsolidationNumber());
        params.put("activationDate", determination.getActivationDate());
        params.put("variationScheduleItems", Stream.concat(
        		variationDetailsDecision != null ? variationDetailsDecision.getVariationScheduleItems().stream() : Collections.emptyList().stream(),
						reviewGroupsVariationScheduleItems.stream()).collect(Collectors.toList()));
        params.put("detailsReason", payload.getPermitVariationDetails().getReason());
        params.put("reasonTemplate", determination.getReasonTemplate());
        params.put("reasonTemplateOther", determination.getReasonTemplateOtherSummary());
        params.put("feeRequired", paymentDetermineAmountServiceFacade.resolveAmount(requestId).compareTo(BigDecimal.ZERO) > 0);
        return params;
	}

}
