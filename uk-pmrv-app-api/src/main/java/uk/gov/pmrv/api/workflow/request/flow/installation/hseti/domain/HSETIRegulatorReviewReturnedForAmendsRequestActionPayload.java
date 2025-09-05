package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HSETIRegulatorReviewReturnedForAmendsRequestActionPayload extends RequestActionPayload {

    @Builder.Default
    private Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(HSETIReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getRegulatorReviewAttachments();
    }
}
