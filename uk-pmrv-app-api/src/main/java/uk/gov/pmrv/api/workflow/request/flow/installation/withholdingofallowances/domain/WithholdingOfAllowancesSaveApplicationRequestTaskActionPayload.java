package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private WithholdingOfAllowances withholdingOfAllowances;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
