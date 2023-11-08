package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private Boolean reportingRequired;

    private AviationAerReportingObligationDetails reportingObligationDetails;

    private AviationAerUkEts aer;

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();
}
