package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.*;

import java.util.EnumMap;
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
public class BDRS2ApplicationCompletedRequestActionPayload extends BDRS2ApplicationSubmittedRequestActionPayload {

    private BDRS2 verifiedBdrs2;

    private BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<BDRS2ReviewGroup, BDRS2ReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRS2ReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), regulatorReviewAttachments)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
