package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AirApplicationReviewRequestTaskPayload extends AirApplicationSubmitRequestTaskPayload
    implements RequestTaskPayloadRfiAttachable {

    @NotNull
    @Valid
    private RegulatorAirReviewResponse regulatorReviewResponse;

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    // Attachments for the rfi are temporarily stored here.
    // The getReferencedAttachmentIds does not include this, which means that on task completion
    // all the files in this map will be deleted.
    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        
        return Stream.of(super.getAttachments(), this.getReviewAttachments(), this.getRfiAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> improvementUuids = this.getRegulatorReviewResponse() == null ? 
            Set.of() :
            this.getRegulatorReviewResponse()
            .getRegulatorImprovementResponses().values().stream()
            .map(RegulatorAirImprovementResponse::getFiles)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        
        return Stream.of(super.getReferencedAttachmentIds(), improvementUuids)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        super.removeAttachments(uuids);
        this.getReviewAttachments().keySet().removeIf(uuids::contains);
        this.getRfiAttachments().keySet().removeIf(uuids::contains);
    }
}
