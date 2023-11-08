package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateAviationAersHandlerTest {

    @InjectMocks
    private InitiateAviationAersHandler initiateAviationAersHandler;

    @Mock
    private AviationAerCreationService aviationAerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_auto_without_provided_accounts() throws Exception {
        Long accountId1 = 1L;
        List<AviationAccountStatus> validAccountStatuses = List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE);
        when(execution.getVariable("accountId")).thenReturn(accountId1);

        // Invoke
        initiateAviationAersHandler.execute(execution);

        // Verify
        verify(aviationAerCreationService, timeout(1000).times(1)).createRequestAviationAer(accountId1);
    }

    @Test
    void execute_with_exception() throws Exception {
        Long accountId1 = 1L;

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
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