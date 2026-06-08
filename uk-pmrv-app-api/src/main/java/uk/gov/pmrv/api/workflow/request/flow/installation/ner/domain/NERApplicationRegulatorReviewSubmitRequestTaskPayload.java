package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERNerDataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;

import java.util.Collection;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NERApplicationRegulatorReviewSubmitRequestTaskPayload extends  NERApplicationVerificationSubmitRequestTaskPayload {

    private NER verifiedNER;

    @NotNull
    private NERApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<NERReviewGroup, NERReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(NERReviewGroup.class);

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
                .filter(decision -> decision.getReviewDataType().equals(NERReviewDataType.NER_DATA))
                .map(NERNerDataRegulatorReviewDecision.class::cast)
                .filter(reviewDecision -> reviewDecision.getType() == NERNerDataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .flatMap(nerDataRegulatorReviewDecision ->
                        ((NERNerDataRegulatorReviewOperatorAmendsNeededDecisionDetails) nerDataRegulatorReviewDecision.getDetails()).getRequiredChanges().stream()
                                .map(NERNerDataRegulatorReviewRequiredChange::getFiles))
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
