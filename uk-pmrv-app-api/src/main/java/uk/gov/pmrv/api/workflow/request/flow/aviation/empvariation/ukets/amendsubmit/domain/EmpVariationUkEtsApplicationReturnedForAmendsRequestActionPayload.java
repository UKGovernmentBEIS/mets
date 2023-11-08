package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload extends RequestActionPayload {

	@Builder.Default
    private Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
	
	private EmpVariationReviewDecision empVariationDetailsReviewDecision;

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getReviewAttachments();
    }
}
