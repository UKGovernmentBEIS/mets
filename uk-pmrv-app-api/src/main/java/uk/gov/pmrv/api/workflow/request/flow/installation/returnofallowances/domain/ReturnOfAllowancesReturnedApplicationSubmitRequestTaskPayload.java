package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    private ReturnOfAllowancesReturned returnOfAllowancesReturned;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
