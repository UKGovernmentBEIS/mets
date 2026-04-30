package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateALRHandlerFlowableTest {

    @Mock
    private ALRCreationService alrCreationService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private InitiateALRHandlerFlowable handler;

    @Captor
    private ArgumentCaptor<Optional<Date>> expirationOptCaptor;

    @Test
    void execute_whenExpirationDateProvided_callsCreateALRWithOptionalOfDate() {
        Long accountId = 100L;
        boolean isFinal = true;
        Date expiration = new Date();

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.ALR_FINAL)).thenReturn(isFinal);
        when(execution.getVariable(BpmnProcessConstants.ALR_EXPIRATION_DATE)).thenReturn(expiration);

        handler.execute(execution);

        verify(alrCreationService).createALRInNewTransaction(eq(accountId), eq(isFinal), expirationOptCaptor.capture());
        assertThat(expirationOptCaptor.getValue()).contains(expiration);
    }

    @Test
    void execute_whenExpirationDateNull_callsCreateALRWithOptionalEmpty() {
        Long accountId = 101L;
        boolean isFinal = false;

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.ALR_FINAL)).thenReturn(isFinal);
        when(execution.getVariable(BpmnProcessConstants.ALR_EXPIRATION_DATE)).thenReturn(null);

        handler.execute(execution);

        verify(alrCreationService).createALRInNewTransaction(eq(accountId), eq(isFinal), expirationOptCaptor.capture());
        assertThat(expirationOptCaptor.getValue()).isEmpty();
    }

    @Test
    void execute_whenCreateALRThrows_doesNotPropagateException() {
        Long accountId = 102L;
        boolean isFinal = true;
        Date expiration = new Date();

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.ALR_FINAL)).thenReturn(isFinal);
        when(execution.getVariable(BpmnProcessConstants.ALR_EXPIRATION_DATE)).thenReturn(expiration);

        doThrow(new RuntimeException("boom"))
                .when(alrCreationService)
                .createALRInNewTransaction(eq(accountId), eq(isFinal), any());

        assertThatCode(() -> handler.execute(execution)).doesNotThrowAnyException();

        verify(alrCreationService).createALRInNewTransaction(eq(accountId), eq(isFinal), expirationOptCaptor.capture());
        assertThat(expirationOptCaptor.getValue()).contains(expiration);
    }
}
