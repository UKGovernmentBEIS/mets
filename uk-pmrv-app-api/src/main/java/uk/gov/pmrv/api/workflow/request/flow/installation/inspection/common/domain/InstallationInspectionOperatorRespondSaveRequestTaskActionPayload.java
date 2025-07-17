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
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;



@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationInspectionOperatorRespondSaveRequestTaskActionPayload extends RequestTaskActionPayload {
    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid FollowUpActionResponse> followupActionsResponses = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> installationInspectionOperatorRespondSectionsCompleted = new HashMap<>();

}
