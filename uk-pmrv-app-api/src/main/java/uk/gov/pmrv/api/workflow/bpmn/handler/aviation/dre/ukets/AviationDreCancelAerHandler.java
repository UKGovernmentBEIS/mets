package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.dre.ukets;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service.AviationDreCancelAerService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationDreCancelAerHandler  implements JavaDelegate {

    private final AviationDreCancelAerService cancelAerService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        cancelAerService.process(requestId);
    }
}
