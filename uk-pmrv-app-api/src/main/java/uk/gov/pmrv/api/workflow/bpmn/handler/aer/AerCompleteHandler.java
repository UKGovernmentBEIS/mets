package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCompleteService;

@Service
@RequiredArgsConstructor
public class AerCompleteHandler implements JavaDelegate {

    private final AerCompleteService aerCompleteService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aerCompleteService.complete(requestId);
    }
}
