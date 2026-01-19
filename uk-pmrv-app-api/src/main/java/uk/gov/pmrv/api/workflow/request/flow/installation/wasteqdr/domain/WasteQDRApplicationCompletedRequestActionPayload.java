package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

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
public class WasteQDRApplicationCompletedRequestActionPayload extends WasteQDRApplicationSubmittedRequestActionPayload {

    private WasteQDRReviewDecision reviewDecision;

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), regulatorReviewAttachments)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
