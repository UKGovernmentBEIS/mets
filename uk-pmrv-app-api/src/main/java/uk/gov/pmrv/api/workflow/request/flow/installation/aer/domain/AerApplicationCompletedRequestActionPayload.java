package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AerApplicationCompletedRequestActionPayload extends AerApplicationSubmittedRequestActionPayload {

    private Aer verifiedAer;

    @Builder.Default
    private Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AerReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    private AerSkipReviewDecision aerSkipReviewDecision;

}
