package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails extends NERRegulatorReviewDecisionDetails {

    private Boolean verificationRequired;

    @NotEmpty
    @Valid
    @Builder.Default
    private List<NERNerDataRegulatorReviewRequiredChange> requiredChanges = new ArrayList<>();
}
