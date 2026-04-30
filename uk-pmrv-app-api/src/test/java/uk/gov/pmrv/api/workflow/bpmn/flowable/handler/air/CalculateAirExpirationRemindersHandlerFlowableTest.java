package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirExpirationRemindersService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAirExpirationRemindersHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private CalculateAirExpirationRemindersService expirationRemindersService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @InjectMocks
    private CalculateAirExpirationRemindersHandlerFlowable handler;

    @Test
    void execute_setsExpirationVarsOnExecution() {
        LocalDate expirationDate = LocalDate.of(2022, 7, 1);
        Date dueDate = DateUtils.atEndOfDay(expirationDate);
        Map<String, Object> expirations = Map.of("airFirstReminderDate", dueDate, "airSecondReminderDate", dueDate);

        when(expirationRemindersService.getExpirationDate()).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, dueDate)).thenReturn(expirations);

        handler.execute(execution);

        verify(execution).setVariables(expirations);
    }
}
