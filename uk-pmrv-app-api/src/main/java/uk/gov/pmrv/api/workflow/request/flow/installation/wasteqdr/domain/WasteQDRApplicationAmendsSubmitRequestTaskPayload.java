package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WasteQDRApplicationAmendsSubmitRequestTaskPayload extends WasteQDRApplicationSubmitRequestTaskPayload {

    private WasteQDRReviewDecision reviewDecision;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getRegulatorReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
