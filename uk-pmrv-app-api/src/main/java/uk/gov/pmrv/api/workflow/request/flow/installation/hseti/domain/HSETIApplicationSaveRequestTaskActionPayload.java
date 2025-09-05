package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

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
public class HSETIApplicationSaveRequestTaskActionPayload extends RequestTaskActionPayload  {

    private HSETI hseti;

    @Builder.Default
    private Map<String, Boolean> hsetiSectionsCompleted = new HashMap<>();
}
