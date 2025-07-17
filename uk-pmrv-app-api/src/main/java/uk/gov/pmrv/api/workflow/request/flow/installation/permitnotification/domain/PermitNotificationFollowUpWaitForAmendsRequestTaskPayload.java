package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
public class PermitNotificationFollowUpWaitForAmendsRequestTaskPayload extends RequestTaskPayload {

    @NotBlank
    @Size(max = 10000)
    private String followUpRequest;

    @NotBlank
    @Size(max = 10000)
    private String followUpResponse;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> followUpFiles = new HashSet<>();

    @NotNull
    private PermitNotificationFollowUpReviewDecision reviewDecision;

    @Builder.Default
    private Map<UUID, String> followUpResponseAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.followUpResponseAttachments;
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reviewFiles =
            this.reviewDecision != null && reviewDecision.getType() == PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED
                ? ((PermitNotificationFollowupRequiredChangesDecisionDetails) reviewDecision.getDetails()).getRequiredChanges().stream()
                .map(
                    ReviewDecisionRequiredChange::getFiles).flatMap(Collection::stream).collect(Collectors.toSet()) : Set.of();

        return Stream.of(reviewFiles, this.followUpFiles).flatMap(Set::stream).collect(Collectors.toSet());
    }
}
