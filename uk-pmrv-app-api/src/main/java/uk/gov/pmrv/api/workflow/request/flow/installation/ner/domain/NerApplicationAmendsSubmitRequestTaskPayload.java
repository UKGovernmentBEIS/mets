package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class NerApplicationAmendsSubmitRequestTaskPayload extends NerApplicationSubmitRequestTaskPayload {
    
    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<NerReviewGroup, NerReviewGroupDecision> reviewGroupDecisions = new EnumMap<>(NerReviewGroup.class);


    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), this.getReviewAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reviewAttachmentIds = this. getReviewGroupDecisions().values().stream()
            .filter(reviewDecision -> reviewDecision.getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .flatMap(decision ->
                ((ChangesRequiredDecisionDetails) decision.getDetails()).getRequiredChanges().stream()
                    .map(ReviewDecisionRequiredChange::getFiles))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        return Stream.of(super.getReferencedAttachmentIds(), reviewAttachmentIds)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        this.getReviewAttachments().keySet().removeIf(uuids::contains);
    }
}
