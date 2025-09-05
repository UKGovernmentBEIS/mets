package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.constraints.NotNull;
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
@SuperBuilder
public class ALRApplicationAmendsSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private ALR alr;

    @Builder.Default
    private Map<String, Boolean> alrSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();
}
