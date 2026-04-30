package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import org.apache.commons.lang3.time.DateUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateBDRS2ExpirationRemindersHandlerFlowableTest {

    @InjectMocks
    private CalculateBDRS2ExpirationRemindersHandlerFlowable handler;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final Date start = new Date();
        final Date expirationDate = DateUtils.addDays(start, 10);
        final Map<String, Object> vars = Map.of("var1", "val1");

        when(execution.getVariable(BpmnProcessConstants.BDRS2_EXPIRATION_DATE)).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.BDRS2, expirationDate))
                .thenReturn(vars);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.BDRS2_EXPIRATION_DATE);
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(RequestExpirationType.BDRS2, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }
}
