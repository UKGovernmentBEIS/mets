package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.doe.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaUpdateReportableEmissionsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaUpdateReportableEmissionsHandlerFlowable implements JavaDelegate {

    private final AviationDoECorsiaUpdateReportableEmissionsService aviationDoECorsiaUpdateReportableEmissionsService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aviationDoECorsiaUpdateReportableEmissionsService.updateReportableEmissions(requestId);
    }
}
