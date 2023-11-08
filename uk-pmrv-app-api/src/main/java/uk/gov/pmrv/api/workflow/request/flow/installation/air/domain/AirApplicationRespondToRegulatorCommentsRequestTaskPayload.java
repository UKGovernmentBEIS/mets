package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AirApplicationRespondToRegulatorCommentsRequestTaskPayload extends AirApplicationSubmitRequestTaskPayload {

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid RegulatorAirImprovementResponse> regulatorImprovementResponses = new HashMap<>();

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid OperatorAirImprovementFollowUpResponse> operatorImprovementFollowUpResponses = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<Integer, Boolean> airRespondToRegulatorCommentsSectionsCompleted = new HashMap<>();
    
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Integer> respondedItems = new HashSet<>();

    @Override
    public Map<UUID, String> getAttachments() {

        return Stream.of(super.getAttachments(), this.getReviewAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        return Stream.of(
                super.getReferencedAttachmentIds(),
                this.regulatorImprovementResponses.values().stream()
                    .map(RegulatorAirImprovementResponse::getFiles)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet()),
                this.operatorImprovementFollowUpResponses.values().stream()
                    .map(OperatorAirImprovementFollowUpResponse::getFiles)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())
            ).flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {

        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        super.removeAttachments(uuids);
        this.getReviewAttachments().keySet().removeIf(uuids::contains);
    }
}
