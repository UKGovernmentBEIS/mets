package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
public class BDRS2ApplicationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private BDRS2 bdrs2;

    private int bdrs2FileVersion;

    @Builder.Default
    private Map<String, Boolean> bdrs2SectionsCompleted = new HashMap<>();
}
