package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class EmpVariationReviewDecision {

	@Valid
    @Schema(
            discriminatorMapping = {
                    @DiscriminatorMapping(schema = EmpAcceptedVariationDecisionDetails.class, value = "ACCEPTED"),
                    @DiscriminatorMapping(schema = ReviewDecisionDetails.class, value = "REJECTED"),
                    @DiscriminatorMapping(schema = ChangesRequiredDecisionDetails.class, value = "OPERATOR_AMENDS_NEEDED")
            },
            discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = EmpAcceptedVariationDecisionDetails.class, name = "ACCEPTED"),
        @JsonSubTypes.Type(value = ReviewDecisionDetails.class, name = "REJECTED"),
        @JsonSubTypes.Type(value = ChangesRequiredDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private ReviewDecisionDetails details;
	
	@NotNull
    private EmpVariationReviewDecisionType type;
}
