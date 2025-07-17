package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @NotEmpty
    @JsonDeserialize(as = LinkedHashMap.class)
    @Builder.Default
    private Map<Integer, @NotNull @Valid AirImprovement> airImprovements = new HashMap<>();

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid OperatorAirImprovementResponse> operatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<Integer, Boolean> airSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> airAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getAirAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        return operatorImprovementResponses.isEmpty()
            ? Collections.emptySet()
            : operatorImprovementResponses.values().stream()
            .map(OperatorAirImprovementResponse::getAirFiles)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }
}
