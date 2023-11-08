package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WithholdingWithdrawal {

    @NotEmpty
    @Size(max = 10000)
    private String reason;
}
