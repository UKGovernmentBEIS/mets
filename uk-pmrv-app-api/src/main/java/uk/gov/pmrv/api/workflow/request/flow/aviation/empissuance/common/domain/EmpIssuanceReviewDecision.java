package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpIssuanceReviewDecision {

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = ReviewDecisionDetails.class, name = "ACCEPTED"),
        @JsonSubTypes.Type(value = ChangesRequiredDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private ReviewDecisionDetails details;

    @NotNull
    private EmpReviewDecisionType type;
}
