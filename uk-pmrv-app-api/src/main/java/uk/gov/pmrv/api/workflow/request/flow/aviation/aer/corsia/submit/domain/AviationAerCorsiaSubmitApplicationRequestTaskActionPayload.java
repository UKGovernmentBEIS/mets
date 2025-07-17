package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaSubmitApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();
}
