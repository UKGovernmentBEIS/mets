package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ALRApplicationRequestTaskPayload extends RequestTaskPayload {

    private ALR alr;

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();


    @Builder.Default
    private Map<String, Boolean> alrSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> alrAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getAlrAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getAlrAttachments().keySet(),
                        this.getAlr().getFiles(),
                        Set.of(this.getAlr().getAlrFile()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
