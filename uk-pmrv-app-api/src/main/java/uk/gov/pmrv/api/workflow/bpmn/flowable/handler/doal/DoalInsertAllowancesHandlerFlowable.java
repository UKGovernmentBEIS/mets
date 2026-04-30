package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.doal;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAllowancesService;

@Service
@RequiredArgsConstructor
public class DoalInsertAllowancesHandlerFlowable implements JavaDelegate {

    private final DoalAllowancesService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.insertAllowanceValues((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
