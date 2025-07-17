package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
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
public class AviationAerUkEtsApplicationCompletedRequestActionPayload extends AviationAerUkEtsApplicationSubmittedRequestActionPayload {

    @Builder.Default
    private Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();
}
