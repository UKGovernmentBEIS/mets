package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

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
public class InstallationInspectionOperatorRespondRequestTaskPayload
        extends RequestTaskPayload {

    private InstallationInspection installationInspection;

    @Builder.Default
    private Map<String, Boolean> installationInspectionOperatorRespondSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> inspectionAttachments = new HashMap<>();

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid FollowUpActionResponse> followupActionsResponses = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getInspectionAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getInspectionAttachments().keySet(),
                        this.getInstallationInspection().getDetails().getFiles())
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
