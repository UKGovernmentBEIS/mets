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
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstallationInspectionOperatorRespondedRequestActionPayload extends RequestActionPayload {

    private InstallationInspection installationInspection;

    @Builder.Default
    private Map<UUID, String> inspectionAttachments = new HashMap<>();

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid FollowUpActionResponse> followupActionsResponses = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getInspectionAttachments();
    }
}
