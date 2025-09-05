package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


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
public class ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails extends ALRRegulatorReviewDecisionDetails {

    private Boolean verificationRequired;

    @NotEmpty
    @Valid
    @Builder.Default
    private List<ALRAlrDataRegulatorReviewRequiredChange> requiredChanges = new ArrayList<>();
}
