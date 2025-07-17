package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BDRApplicationCompletedRequestActionPayload extends BDRApplicationSubmittedRequestActionPayload {

    private BDR verifiedBdr;

    private BDRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<BDRReviewGroup, BDRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

}
