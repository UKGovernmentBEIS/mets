package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class ALRSaveRegulatorReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private ALRReviewGroup group;

    @NotNull
    @Valid
    private ALRReviewDecision decision;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

}
