package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
public class BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload extends BDRS2ApplicationVerificationSubmitRequestTaskPayload {

    private BDRS2 verifiedBdrs2;

    @NotNull
    private BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRS2ReviewGroup.class);

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
                .filter(decision -> decision.getReviewDataType().equals(BDRS2ReviewDataType.BDRS2_DATA))
                .map(BDRS2Bdrs2DataRegulatorReviewDecision.class::cast)
                .filter(reviewDecision -> reviewDecision.getType() == BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .flatMap(bdrs2DataReviewDecision ->
                        ((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails) bdrs2DataReviewDecision.getDetails()).getRequiredChanges().stream()
                                .map(BDRS2Bdrs2DataRegulatorReviewRequiredChange::getFiles))
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
