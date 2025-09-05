package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIExpirationDateService;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateHSETIExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateHSETIExpirationRemindersHandler handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private DelegateExecution execution;

    @Mock
    private HSETIExpirationDateService hsetiExpirationDateService;

    @Test
    void execute() {

        final Date start = new Date();
        final Date expirationDate = DateUtils.addDays(start, 14);

        final Map<String, Object> vars = Map.of(
                "var1", "val1"
        );

        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.HSETI, expirationDate))
                .thenReturn(vars);
        when(hsetiExpirationDateService.calculateExpirationDate()).thenReturn(expirationDate);

        handler.execute(execution);

        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(RequestExpirationType.HSETI, expirationDate);
        verify(execution, times(1)).setVariables(vars);
        verify(hsetiExpirationDateService, times(1)).calculateExpirationDate();
    }
}
