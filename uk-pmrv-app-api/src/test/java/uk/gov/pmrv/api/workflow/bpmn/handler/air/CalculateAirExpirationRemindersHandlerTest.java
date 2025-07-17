package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirExpirationRemindersService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class CalculateAirExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateAirExpirationRemindersHandler handler;

    @Mock
    private CalculateAirExpirationRemindersService expirationRemindersService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private DelegateExecution execution;
    
    @Test
    void execute() throws Exception {

        final LocalDate expirationDate = LocalDate.of(2022, 7, 1);
        final Date dueDate = DateUtils.atEndOfDay(expirationDate);
        final Map<String, Object> expirations = Map.of("expirationDate", dueDate);

        when(expirationRemindersService.getExpirationDate()).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, dueDate)).thenReturn(expirations);
        
        handler.execute(execution);

        verify(execution, times(1)).setVariables(expirations);
    }
}
