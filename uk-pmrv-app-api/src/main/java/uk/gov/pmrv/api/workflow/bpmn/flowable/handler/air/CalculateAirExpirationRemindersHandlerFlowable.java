package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirExpirationRemindersService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalculateAirExpirationRemindersHandlerFlowable implements JavaDelegate {

    private final CalculateAirExpirationRemindersService expirationRemindersService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {
        final Date dueDate = DateUtils.atEndOfDay(expirationRemindersService.getExpirationDate());
        final Map<String, Object> expirationVars =
                requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, dueDate);

        execution.setVariables(expirationVars);
    }
}
