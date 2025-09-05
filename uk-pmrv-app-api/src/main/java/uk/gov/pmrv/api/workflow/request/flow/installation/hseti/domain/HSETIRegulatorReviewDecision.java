package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Builder
public class HSETIRegulatorReviewDecision {

    @NotNull
    private HSETIRegulatorReviewDecisionType type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = HSETIRegulatorReviewDecisionAcceptedDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = HSETIRegulatorReviewDecisionRejectedDetails.class, name = "REJECTED"),
            @JsonSubTypes.Type(value = HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private HSETIRegulatorReviewDecisionDetails details;

}
