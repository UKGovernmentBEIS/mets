package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Collection;
import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ALRApplicationRegulatorReviewSubmitRequestTaskPayload extends ALRApplicationVerificationSubmitRequestTaskPayload {


    private ALR verifiedAlr;

    @NotNull
    @Valid
    private ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome;


    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<ALRReviewGroup, ALRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(ALRReviewGroup.class);

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
                .filter(decision -> decision.getReviewDataType().equals(ALRReviewDataType.ALR_DATA))
                .map(ALRAlrDataRegulatorReviewDecision.class::cast)
                .filter(reviewDecision -> reviewDecision.getType() == ALRAlrDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .flatMap(alrDataReviewDecision ->
                        ((ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails) alrDataReviewDecision.getDetails()).getRequiredChanges().stream()
                                .map(ALRAlrDataRegulatorReviewRequiredChange::getFiles))
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
