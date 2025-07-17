package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InitiateALRHandlerTest {

    @InjectMocks
    private InitiateALRHandler handler;

    @Mock
    private ALRCreationService alrCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        Long accountId = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId);

        handler.execute(execution);

        verify(alrCreationService, timeout(1000).times(1)).createALR(accountId);
    }

    @Test
    void execute_exception() throws Exception {
        Long accountId = 1L;

        when(execution.getVariable("accountId")).thenReturn(accountId);
        when(alrCreationService.createALR(accountId))
                .thenThrow(new BusinessException(
                        MetsErrorCode.ALR_CREATION_NOT_ALLOWED,
                        RequestCreateValidationResult.builder().valid(false).build()));

        handler.execute(execution);

        verify(alrCreationService, timeout(1000).times(1)).createALR(accountId);
    }
}
