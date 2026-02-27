package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
public class BDRS2Bdrs2DataRegulatorReviewDecision extends BDRS2ReviewDecision {

    @NotNull
    private BDRS2Bdrs2DataRegulatorReviewDecisionType type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BDRS2Bdrs2DataRegulatorReviewAcceptedDecisionDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private BDRS2RegulatorReviewDecisionDetails details;
}
