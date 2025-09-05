package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HSETIRegulatorReviewDecisionDetails {

    @NotNull
    @NotEmpty
    @Size(max = 10000)
    private String capacityIncreaseDescription;

    @NotNull
    @NotEmpty
    @Size(max = 10000)
    private String capacityIncreasePermanence;

    @NotNull
    @NotEmpty
    @Size(max = 10000)
    private String capacityGreaterThanZeroDescription;

    @Size(max = 10000)
    private String notes;
}