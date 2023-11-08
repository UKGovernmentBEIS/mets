package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload extends RequestActionPayload {

	@Builder.Default
	@NotEmpty
    private Map<EmpCorsiaReviewGroup, @Valid EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getReviewAttachments();
    }
}
