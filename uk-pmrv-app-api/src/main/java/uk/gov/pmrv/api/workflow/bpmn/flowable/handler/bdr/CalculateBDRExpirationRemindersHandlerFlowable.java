package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CalculateBDRExpirationRemindersHandlerFlowable implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {
        final Date expirationDate = (Date) execution.getVariable(BpmnProcessConstants.BDR_EXPIRATION_DATE);
        execution.setVariables(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.BDR, expirationDate));
    }
}
