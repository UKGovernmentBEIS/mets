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
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationInspectionRequestPayload extends RequestPayload {

    private InstallationInspection installationInspection;

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid FollowUpActionResponse> followupActionsResponses = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> inspectionAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> installationInspectionOperatorRespondSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;
}
