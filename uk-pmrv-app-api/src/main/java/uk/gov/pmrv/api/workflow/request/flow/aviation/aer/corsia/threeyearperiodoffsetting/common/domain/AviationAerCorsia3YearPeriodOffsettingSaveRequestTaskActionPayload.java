package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain;

import java.util.HashMap;
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
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting;

    @Builder.Default
    private Map<String, Boolean> aviationAerCorsia3YearPeriodOffsettingSectionsCompleted = new HashMap<>();
}
