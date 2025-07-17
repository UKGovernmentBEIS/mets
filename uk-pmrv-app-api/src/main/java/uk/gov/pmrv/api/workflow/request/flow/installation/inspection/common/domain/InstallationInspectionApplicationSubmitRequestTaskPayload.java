package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;


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
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstallationInspectionApplicationSubmitRequestTaskPayload extends RequestTaskPayload {


    private InstallationInspection installationInspection;

    @Builder.Default
    private Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> inspectionAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getInspectionAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(super.getReferencedAttachmentIds(),
                        this.getInspectionAttachments().keySet(),
                        this.getInstallationInspection().getDetails().getFiles(),
                        this.getInstallationInspection().getDetails().getRegulatorExtraFiles())
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
