package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;


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
public class BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails extends BDRS2RegulatorReviewDecisionDetails {

    private Boolean verificationRequired;

    @NotEmpty
    @Valid
    @Builder.Default
    private List<BDRS2Bdrs2DataRegulatorReviewRequiredChange> requiredChanges = new ArrayList<>();
}
