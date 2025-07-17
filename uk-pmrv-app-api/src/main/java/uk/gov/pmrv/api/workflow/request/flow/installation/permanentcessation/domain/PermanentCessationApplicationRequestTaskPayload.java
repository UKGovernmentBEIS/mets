package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermanentCessationApplicationRequestTaskPayload extends RequestTaskPayload {

    private PermanentCessation permanentCessation;

    @Builder.Default
    private Map<String, Boolean> permanentCessationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> permanentCessationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return permanentCessationAttachments;
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getPermanentCessationAttachments().keySet(),
                        this.getPermanentCessation().getFiles())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
