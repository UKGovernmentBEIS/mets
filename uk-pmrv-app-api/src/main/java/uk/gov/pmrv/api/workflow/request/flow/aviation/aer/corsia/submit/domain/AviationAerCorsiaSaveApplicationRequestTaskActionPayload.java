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
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private Boolean reportingRequired;

    private AviationAerReportingObligationDetails reportingObligationDetails;

    private AviationAerCorsia aer;

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();
}
