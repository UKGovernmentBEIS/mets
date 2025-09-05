package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAllowancesService;

@Service
@RequiredArgsConstructor
public class AlrInsertAllowancesHandler implements JavaDelegate {

    private final ALRAllowancesService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.insertAllowanceValues((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
