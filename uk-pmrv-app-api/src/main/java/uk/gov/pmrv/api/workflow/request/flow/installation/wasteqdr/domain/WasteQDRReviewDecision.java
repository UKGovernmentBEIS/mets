package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteQDRReviewDecision {

    @NotNull
    private WasteQDRReviewDecisionType type;

    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = WasteQDRReviewAcceptedDecisionDetails.class, name = "ACCEPTED"),
            @JsonSubTypes.Type(value = WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
    })
    private WasteQDRRegulatorReviewDecisionDetails details;
}
