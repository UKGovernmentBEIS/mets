package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ALRAlrDataRegulatorReviewAcceptedDecisionDetails.class, name = "ACCEPTED"),
        @JsonSubTypes.Type(value = ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails.class, name = "OPERATOR_AMENDS_NEEDED")
})
public abstract class ALRRegulatorReviewDecisionDetails {

    @Size(max = 10000)
    private String notes;
}
