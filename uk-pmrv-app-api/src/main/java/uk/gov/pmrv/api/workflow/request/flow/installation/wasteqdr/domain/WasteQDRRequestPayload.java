package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class WasteQDRRequestPayload extends RequestPayload {

    @NotNull
    private WasteQDR qdr;

    @Builder.Default
    private Map<UUID, String> wasteQDRAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> wasteQDRSectionsCompleted = new HashMap<>();

    @NotNull
    private WasteQDRReviewDecision reviewDecision;

    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();
}
