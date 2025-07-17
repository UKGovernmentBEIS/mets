package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class BDRRegulatorReviewReturnedForAmendsRequestActionPayload extends RequestActionPayload {

    @Builder.Default
    @NotEmpty
    private Map<BDRReviewGroup, @Valid BDRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getRegulatorReviewAttachments();
    }
}
