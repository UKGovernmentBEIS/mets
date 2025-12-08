package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WasteQDRApplicationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private WasteQDR qdr;

    @Builder.Default
    private Map<String, Boolean> wasteQDRSectionsCompleted = new HashMap<>();
}
