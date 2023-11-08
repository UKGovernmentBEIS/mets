package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitVariationSaveReviewGroupDecisionAbstractRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @Valid
    private PermitVariationReviewDecision decision;
	
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

}
