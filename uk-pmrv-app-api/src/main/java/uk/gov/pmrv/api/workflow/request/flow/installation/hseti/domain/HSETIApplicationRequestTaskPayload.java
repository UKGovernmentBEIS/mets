package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HSETIApplicationRequestTaskPayload extends RequestTaskPayload {

    private HSETI hseti;

    @Builder.Default
    private Map<String, Boolean> hsetiSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> hsetiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getHsetiAttachments();
    }


    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        Set<UUID> extraFiles = new HashSet<>();

        if (this.getHseti()!=null) {
            extraFiles.add(this.getHseti().getHsetiFile());
            extraFiles.addAll(this.getHseti().getFiles());
        }

        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getHsetiAttachments().keySet(),
                        extraFiles)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
