package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.common;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateAviationAersHandlerFlowableTest {

    @InjectMocks
    private InitiateAviationAersHandlerFlowable initiateAviationAersHandler;

    @Mock
    private AviationAerCreationService aviationAerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_auto_without_provided_accounts() {
        Long accountId1 = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId1);

        // Invoke
        initiateAviationAersHandler.execute(execution);

        // Verify
        verify(aviationAerCreationService, timeout(1000).times(1)).createRequestAviationAer(accountId1);
    }

    @Test
    void execute_with_exception() {
        Long accountId1 = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId1);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(aviationAerCreationService)
                .createRequestAviationAer(accountId1);

        // Invoke
        initiateAviationAersHandler.execute(execution);

        // Verify
        verify(aviationAerCreationService, timeout(1000).times(1)).createRequestAviationAer(accountId1);
        verifyNoMoreInteractions(aviationAerCreationService);
    }
}