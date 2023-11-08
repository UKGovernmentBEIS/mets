package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirExpirationRemindersService;

@Service
@RequiredArgsConstructor
public class CalculateAirExpirationRemindersHandler implements JavaDelegate {

    private final CalculateAirExpirationRemindersService expirationRemindersService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final LocalDate expirationDate = expirationRemindersService.getExpirationDate();
        final Date dueDate = Date.from(
            expirationDate
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
        final Map<String, Object> expirationVars =
            requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, dueDate);

        execution.setVariables(expirationVars);
    }
}
