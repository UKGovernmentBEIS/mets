package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRPreliminaryAllocation extends PreliminaryAllocation {

    @NotEmpty
    private String allocationId;
}
