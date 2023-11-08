package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VirSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @Builder.Default
    private Map<String, OperatorImprovementResponse> operatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> virSectionsCompleted = new HashMap<>();
}
