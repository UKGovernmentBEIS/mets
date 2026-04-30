package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateInstallationAersHandlerFlowableTest {

    @InjectMocks
    private InitiateInstallationAersHandlerFlowable initiateInstallationAersHandler;

    @Mock
    private AerCreationService aerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeAutomaticWorkflow() throws Exception {
        Long accountId1 = 1L;
        when(execution.getVariable("accountId")).thenReturn(accountId1);

        // Invoke
        initiateInstallationAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAerInNewTransaction(accountId1, RequestType.AER);
        verifyNoMoreInteractions(aerCreationService);
    }

    @Test
    void execute_with_exception() throws Exception {
        Long accountId1 = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId1);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(aerCreationService)
            .createRequestAerInNewTransaction(accountId1, RequestType.AER);

        // Invoke
        initiateInstallationAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAerInNewTransaction(accountId1, RequestType.AER);
        verifyNoMoreInteractions(aerCreationService);
    }
}
