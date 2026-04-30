package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIExpirationDateService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CalculateHSETIExpirationRemindersHandlerFlowable implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final HSETIExpirationDateService hsetiExpirationDateService;

    @Override
    public void execute(DelegateExecution execution) {
        final Date expirationDate = hsetiExpirationDateService.calculateExpirationDate();
        execution.setVariables(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.HSETI, expirationDate));
    }
}
