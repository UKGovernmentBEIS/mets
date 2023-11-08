package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload extends RequestTaskActionPayload {

    private String reasonRegulatorLed;

    private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;

    private EmpVariationCorsiaDetails empVariationDetails;

    private Boolean empVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();
}
