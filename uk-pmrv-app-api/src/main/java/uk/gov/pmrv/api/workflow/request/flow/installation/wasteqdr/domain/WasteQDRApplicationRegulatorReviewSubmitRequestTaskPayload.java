package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;


import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload extends RequestTaskPayload {

    private WasteQDR qdr;

    private WasteQDRReviewDecision reviewDecision;

    @Builder.Default
    private Map<String, Boolean> wasteQDRSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> wasteQDRAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getRegulatorReviewAttachments(), getWasteQDRAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        Set<UUID> result = new HashSet<>();

        result.addAll(super.getReferencedAttachmentIds());

        result.addAll(getRegulatorReviewAttachments().keySet());

        if (getReviewDecision().getDetails() instanceof
                WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails operatorAmendsNeededDecisionDetails) {

            operatorAmendsNeededDecisionDetails.getRequiredChanges().stream()
                    .flatMap(rc -> rc.getFiles().stream())
                    .forEach(result::add);
        }

        return result;
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getRegulatorReviewAttachments().keySet().removeIf(uuids::contains);
    }
}
