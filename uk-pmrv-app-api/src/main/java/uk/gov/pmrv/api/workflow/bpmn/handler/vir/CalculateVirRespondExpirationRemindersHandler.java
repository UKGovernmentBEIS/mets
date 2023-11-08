package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.CalculateRespondToRegulatorCommentsExpirationDateService;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalculateVirRespondExpirationRemindersHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final CalculateRespondToRegulatorCommentsExpirationDateService calculateRespondToRegulatorCommentsExpirationDateService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = calculateRespondToRegulatorCommentsExpirationDateService.calculateExpirationDate(requestId);

        Map<String, Object> expirationVars = requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.VIR, expirationDate);
        execution.setVariables(expirationVars);
    }
}
