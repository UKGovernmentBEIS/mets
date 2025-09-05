package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.util.List;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ALRApplicationClosedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private ALR alr;

    private ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<ALRReviewGroup, ALRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(ALRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    private boolean verificationPerformed;

    private ALRVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> alrAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> alrSectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        Map<UUID, String> allAttachments = new HashMap<>();
        allAttachments.putAll(alrAttachments);
        allAttachments.putAll(regulatorReviewAttachments);
        allAttachments.putAll(verificationAttachments);
        return allAttachments;
    }
}
