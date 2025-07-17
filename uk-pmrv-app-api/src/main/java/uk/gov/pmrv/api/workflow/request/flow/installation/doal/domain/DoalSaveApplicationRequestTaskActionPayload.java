package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

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
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoalSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private Doal doal;

    @Builder.Default
    private Map<String, Boolean> doalSectionsCompleted = new HashMap<>();
}
