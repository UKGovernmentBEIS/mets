package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BDRApplicationRegulatorReviewSubmitRequestTaskPayload extends BDRApplicationVerificationSubmitRequestTaskPayload {

    private BDR verifiedBdr;

    @NotNull
    @Valid
    private BDRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<BDRReviewGroup, BDRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getRegulatorReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        final Set<UUID> reviewAttachmentIds = getRegulatorReviewGroupDecisions().values().stream()
                .filter(decision -> decision.getReviewDataType().equals(BDRReviewDataType.BDR_DATA))
                .map(BDRBdrDataRegulatorReviewDecision.class::cast)
                .filter(reviewDecision -> reviewDecision.getType() == BDRBdrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .flatMap(bdrDataReviewDecision ->
                        ((BDRBdrDataRegulatorReviewOperatorAmendsNeededDecisionDetails) bdrDataReviewDecision.getDetails()).getRequiredChanges().stream()
                                .map(BDRBdrDataRegulatorReviewRequiredChange::getFiles))
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
