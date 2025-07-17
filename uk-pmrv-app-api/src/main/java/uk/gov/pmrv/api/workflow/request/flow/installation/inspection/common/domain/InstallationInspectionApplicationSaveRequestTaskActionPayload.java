package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

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
public class InstallationInspectionApplicationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private InstallationInspection installationInspection;

    @Builder.Default
    private Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();
}

