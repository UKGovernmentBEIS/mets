package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    private WithholdingOfAllowances withholdingOfAllowances;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
