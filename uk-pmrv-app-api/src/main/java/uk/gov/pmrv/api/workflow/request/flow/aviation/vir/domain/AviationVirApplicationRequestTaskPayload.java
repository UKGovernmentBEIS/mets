package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AviationVirApplicationRequestTaskPayload extends RequestTaskPayload {

    private VirVerificationData verificationData;

    @Builder.Default
    private Map<String, OperatorImprovementResponse> operatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> virSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> virAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getVirAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return operatorImprovementResponses.isEmpty()
                ? Collections.emptySet()
                : operatorImprovementResponses.values().stream()
                .map(OperatorImprovementResponse::getFiles)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
