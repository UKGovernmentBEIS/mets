package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ALRAlrDataRegulatorReviewDecision extends ALRReviewDecision {

    @NotNull
    private ALRAlrDataRegulatorReviewDecisionType type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ALRAlrDataRegulatorReviewAcceptedDecisionDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private ALRRegulatorReviewDecisionDetails details;
}
