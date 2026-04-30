package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2CreationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateBDRS2HandlerFlowableTest {

    @InjectMocks
    private InitiateBDRS2HandlerFlowable handler;

    @Mock
    private BDRS2CreationService bdrs2CreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        Long accountId = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId);

        handler.execute(execution);

        verify(bdrs2CreationService, times(1)).createBDRS2(accountId);
    }

    @Test
    void execute_exception() {
        Long accountId = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId);
        when(bdrs2CreationService.createBDRS2(accountId))
                .thenThrow(new BusinessException(MetsErrorCode.BDR_CREATION_NOT_ALLOWED, RequestCreateValidationResult.builder().valid(false).build()));

        handler.execute(execution);

        verify(bdrs2CreationService, times(1)).createBDRS2(accountId);
    }
}
