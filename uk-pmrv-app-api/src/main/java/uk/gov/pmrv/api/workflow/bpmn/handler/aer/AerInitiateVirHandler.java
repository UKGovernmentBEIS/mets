package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreateVirService;

@Service
@RequiredArgsConstructor
public class AerInitiateVirHandler implements JavaDelegate {

    private final AerCreateVirService aerCreateVirService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aerCreateVirService.createRequestVir(requestId);
    }
}
