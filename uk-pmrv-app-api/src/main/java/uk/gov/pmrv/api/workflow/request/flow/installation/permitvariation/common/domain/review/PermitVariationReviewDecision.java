package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewDecision;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationReviewDecision extends PermitReviewDecision {

    @Valid
    @Schema(
            discriminatorMapping = {
                    @DiscriminatorMapping(schema = PermitAcceptedVariationDecisionDetails.class, value = "ACCEPTED"),
                    @DiscriminatorMapping(schema = ReviewDecisionDetails.class, value = "REJECTED"),
                    @DiscriminatorMapping(schema = ChangesRequiredDecisionDetails.class, value = "OPERATOR_AMENDS_NEEDED")
            },
            discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = PermitAcceptedVariationDecisionDetails.class, name = "ACCEPTED"),
        @JsonSubTypes.Type(value = ReviewDecisionDetails.class, name = "REJECTED"),
        @JsonSubTypes.Type(value = ChangesRequiredDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private ReviewDecisionDetails details;

}
