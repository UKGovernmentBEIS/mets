package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateEmpParamsSourceData {

    private Request request;
    private String signatory;
    private EmissionsMonitoringPlanContainer empContainer;
    private int consolidationNumber;
}
