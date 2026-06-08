package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERNerDataRegulatorReviewDecisionType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NERNerDataRegulatorReviewDecision extends NERReviewDecision {

    @NotNull
    private NERNerDataRegulatorReviewDecisionType type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = NERNerDataRegulatorReviewAcceptedDecisionDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private NERRegulatorReviewDecisionDetails details;
}
