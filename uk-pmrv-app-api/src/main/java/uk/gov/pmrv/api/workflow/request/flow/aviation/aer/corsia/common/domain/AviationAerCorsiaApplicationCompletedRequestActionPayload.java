package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
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
public class AviationAerCorsiaApplicationCompletedRequestActionPayload extends AviationAerCorsiaApplicationSubmittedRequestActionPayload {

    private AviationAerCorsiaVerificationReport verificationReport;

    @Builder.Default
    private Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerCorsiaReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();
}
