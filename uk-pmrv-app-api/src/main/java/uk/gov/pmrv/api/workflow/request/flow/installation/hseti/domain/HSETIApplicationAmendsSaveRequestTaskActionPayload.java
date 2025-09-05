package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import jakarta.validation.constraints.NotNull;
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
@SuperBuilder
public class HSETIApplicationAmendsSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private HSETI hseti;

    @Builder.Default
    private Map<String, Boolean> hsetiSectionsCompleted = new HashMap<>();

    private Boolean requestedChangesCompleted;

}
