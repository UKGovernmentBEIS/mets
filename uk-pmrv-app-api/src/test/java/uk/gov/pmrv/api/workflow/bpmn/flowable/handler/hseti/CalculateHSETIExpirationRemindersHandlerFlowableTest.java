package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIExpirationDateService;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateHSETIExpirationRemindersHandlerFlowableTest {

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private HSETIExpirationDateService hsetiExpirationDateService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private CalculateHSETIExpirationRemindersHandlerFlowable handler;

    @Test
    void execute_shouldSetExpirationVariables() {
        Date expirationDate = new Date(1700000000000L);
        Map<String, Object> vars = Map.of("k1", "v1");

        when(hsetiExpirationDateService.calculateExpirationDate()).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.HSETI, expirationDate))
            .thenReturn(vars);

        handler.execute(execution);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(execution).setVariables(captor.capture());
        assertEquals(vars, captor.getValue());

        verify(hsetiExpirationDateService).calculateExpirationDate();
        verify(requestExpirationVarsBuilder).buildExpirationVars(RequestExpirationType.HSETI, expirationDate);
        verifyNoMoreInteractions(hsetiExpirationDateService, requestExpirationVarsBuilder);
    }
}
