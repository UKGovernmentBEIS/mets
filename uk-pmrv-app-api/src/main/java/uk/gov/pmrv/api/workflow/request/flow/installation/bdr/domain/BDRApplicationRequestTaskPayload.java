package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.List;
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
public abstract class BDRApplicationRequestTaskPayload extends RequestTaskPayload {

    private BDR bdr;

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();


    @Builder.Default
    private Map<String, Boolean> bdrSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> bdrAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getBdrAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getBdrAttachments().keySet(),
                        this.getBdr().getFiles(),
                        this.getBdr().getMmpFiles(),
                        Set.of(this.getBdr().getBdrFile()))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

}

