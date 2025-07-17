package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ALRApplicationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private ALR alr;

    @Builder.Default
    private Map<String, Boolean> alrSectionsCompleted = new HashMap<>();
}
