package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload extends PermitVariationRequestTaskPayload {
	
	private PermitContainer originalPermitContainer;

	private PermitAcceptedVariationDecisionDetails permitVariationDetailsReviewDecision;
	
	@Builder.Default
    private Map<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);
	
	private PermitVariationRegulatorLedGrantDetermination determination;
	
	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
	
}
