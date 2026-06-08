package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;

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
public class NERApplicationCompletedRequestActionPayload extends NERApplicationSubmittedRequestActionPayload {

    private NERApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    @NotEmpty
    private Map<NERReviewGroup, @Valid NERReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(NERReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), regulatorReviewAttachments)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
