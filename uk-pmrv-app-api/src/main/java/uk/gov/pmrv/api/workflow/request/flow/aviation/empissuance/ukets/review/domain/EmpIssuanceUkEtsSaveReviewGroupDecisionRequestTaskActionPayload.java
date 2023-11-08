package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private EmpUkEtsReviewGroup reviewGroup;

    @NotNull
    @Valid
    private EmpIssuanceReviewDecision decision;

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();
}
