package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateInstallationAersHandlerTest {

    @InjectMocks
    private InitiateInstallationAersHandler initiateInstallationAersHandler;

    @Mock
    private AerCreationService aerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeAutomaticWorkflow() throws Exception {
        Long accountId1 = 1L;
        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getVariable("accountId")).thenReturn(accountId1);

        // Invoke
        initiateInstallationAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(accountId1, RequestType.AER);
        verifyNoMoreInteractions(aerCreationService);
    }

    @Test
    void execute_with_exception() throws Exception {
        Long accountId1 = 1L;

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getVariable("accountId")).thenReturn(accountId1);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(aerCreationService)
            .createRequestAer(accountId1, RequestType.AER);

        // Invoke
        initiateInstallationAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(accountId1, RequestType.AER);
        verifyNoMoreInteractions(aerCreationService);
    }
}
