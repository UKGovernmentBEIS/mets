package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NERApplicationAmendsSubmitRequestTaskActionPayload extends RequestTaskActionPayload {

    /**
     * When the operator re-submits the amended ner application, nerSectionsCompleted map
     * should not include statuses of the amend tasks.
     * This has to be done in order to have the correct statuses of the amend tasks in the operator task list
     * in case that the regulator asks for an amend in the same section for a second time.
     */
    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();
}
