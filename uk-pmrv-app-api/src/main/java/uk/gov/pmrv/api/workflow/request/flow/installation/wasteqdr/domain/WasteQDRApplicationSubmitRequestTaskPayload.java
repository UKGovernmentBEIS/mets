package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
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
public class WasteQDRApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    private WasteQDR qdr;

    @Builder.Default
    private Map<String, Boolean> wasteQDRSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> wasteQDRAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getWasteQDRAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(
                        this.getWasteQDRAttachments().keySet(),
                        this.getQdr().getSupportingFiles(),
                        this.getQdr().getReport() != null
                                ? Set.of(this.getQdr().getReport())
                                : Set.<UUID>of()
                )
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
