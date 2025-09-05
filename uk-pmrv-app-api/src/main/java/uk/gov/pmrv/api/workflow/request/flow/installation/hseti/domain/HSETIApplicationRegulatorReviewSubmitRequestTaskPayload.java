package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HSETIApplicationRegulatorReviewSubmitRequestTaskPayload extends HSETIApplicationRequestTaskPayload {

    @NotNull
    private HSETIRegulatorReviewOverallDecision overallDecision;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(HSETIReviewGroup.class);


    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getRegulatorReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reviewAttachmentIds = getRegulatorReviewGroupDecisions().values().stream()
                .filter(reviewDecision -> reviewDecision.getType() == HSETIRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .flatMap(reviewDecision ->
                        ((HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails) reviewDecision.getDetails()).getRequiredChanges().stream()
                                .map(HSETIRegulatorReviewOperatorAmendsRequiredChange::getFiles))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        return Stream.of(super.getReferencedAttachmentIds(), reviewAttachmentIds, getRegulatorReviewAttachments().keySet())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getRegulatorReviewAttachments().keySet().removeIf(uuids::contains);
    }
}
