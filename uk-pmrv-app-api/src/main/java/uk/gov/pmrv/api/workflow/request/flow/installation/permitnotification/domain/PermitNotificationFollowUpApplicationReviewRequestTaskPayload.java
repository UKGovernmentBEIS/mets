package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpApplicationReviewRequestTaskPayload extends PermitNotificationFollowUpRequestTaskPayload {

    @NotNull
    private PermitNotificationType permitNotificationType;

    @NotNull
    @PastOrPresent
    private LocalDate submissionDate;

    @NotNull
    private PermitNotificationFollowUpReviewDecision reviewDecision;

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();


    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reviewFiles =
            reviewDecision != null && reviewDecision.getType() == PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED
                ? ((PermitNotificationFollowupRequiredChangesDecisionDetails) reviewDecision.getDetails()).getRequiredChanges().stream().map(
                ReviewDecisionRequiredChange::getFiles).flatMap(Collection::stream).collect(Collectors.toSet()) : Set.of();

        return Stream.of(super.getReferencedAttachmentIds(), reviewFiles)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
