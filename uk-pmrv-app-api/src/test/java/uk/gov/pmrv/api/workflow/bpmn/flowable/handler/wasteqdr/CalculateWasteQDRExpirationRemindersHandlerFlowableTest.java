package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateWasteQDRExpirationRemindersHandlerFlowableTest {

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private CalculateWasteQDRExpirationRemindersHandlerFlowable handler;

    @Test
    void execute_getsExpirationDate_buildsVars_andSetsThemOnExecution() {
        Date expirationDate = new Date();
        Map<String, Object> vars = Map.of(
                BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE, expirationDate,
                "wasteqdrFirstReminderDate", new Date(),
                "wasteqdrSecondReminderDate", new Date()
        );

        when(execution.getVariable(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE)).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.WASTEQDR, expirationDate))
                .thenReturn(vars);

        handler.execute(execution);

        verify(execution).getVariable(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE);
        verify(requestExpirationVarsBuilder).buildExpirationVars(RequestExpirationType.WASTEQDR, expirationDate);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(execution).setVariables(captor.capture());
        assertSame(vars, captor.getValue());

        verifyNoMoreInteractions(requestExpirationVarsBuilder);
    }
}
