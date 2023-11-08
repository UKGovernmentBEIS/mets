package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirReviewable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AviationVirApplicationReviewRequestTaskPayload extends AviationVirApplicationRequestTaskPayload 
    implements RequestTaskPayloadRfiAttachable, VirReviewable {

    private RegulatorReviewResponse regulatorReviewResponse;

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    // Attachments for the rfi are temporarily stored here.
    // The getReferencedAttachmentIds method is not overridden, which means that on task completion
    // all the files in this map will be deleted.
    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), this.getRfiAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        super.removeAttachments(uuids);
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }
}
