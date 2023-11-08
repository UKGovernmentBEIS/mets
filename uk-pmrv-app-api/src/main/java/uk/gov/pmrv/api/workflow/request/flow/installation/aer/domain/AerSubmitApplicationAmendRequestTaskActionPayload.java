package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerSubmitApplicationAmendRequestTaskActionPayload extends RequestTaskActionPayload {

    /**
     * When the operator re-submits the amended aer application, aerSectionsCompleted map
     * should not include statuses of the amend tasks.
     * This has to be done in order to have the correct statuses of the amend tasks in the operator task list
     * in case that the regulator asks for an amend in the same section for a second time.
     */
    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();
}
