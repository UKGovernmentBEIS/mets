package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class WasteQDRApplicationAmendsSubmitRequestTaskActionPayload extends RequestTaskActionPayload {

    @Builder.Default
    private Map<String, Boolean> wasteQDRSectionsCompleted = new HashMap<>();
}
