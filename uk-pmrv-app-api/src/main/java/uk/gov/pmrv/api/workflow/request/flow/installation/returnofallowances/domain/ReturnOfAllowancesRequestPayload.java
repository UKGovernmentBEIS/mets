package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOfAllowancesRequestPayload extends RequestPayload {

    private ReturnOfAllowances returnOfAllowances;

    private ReturnOfAllowancesReturned returnOfAllowancesReturned;

    @Builder.Default
    private Map<String, Boolean> returnOfAllowancesSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> returnOfAllowancesReturnedSectionsCompleted = new HashMap<>();


    private DecisionNotification decisionNotification;
}
