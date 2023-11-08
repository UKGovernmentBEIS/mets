package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirRespondToRegulatorCommentsExpirationDateService;

@Service
@RequiredArgsConstructor
public class CalculateAirRespondExpirationRemindersHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final CalculateAirRespondToRegulatorCommentsExpirationDateService expirationDateService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = expirationDateService.calculateExpirationDate(requestId);

        final Map<String, Object> expirationVars = 
            requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, expirationDate);
        execution.setVariables(expirationVars);
    }
}
