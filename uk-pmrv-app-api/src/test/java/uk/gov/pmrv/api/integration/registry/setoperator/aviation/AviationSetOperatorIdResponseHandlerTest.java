package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.NotifyErrorDTO;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdErrorNotifierService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdEventOutcomeService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationSetOperatorIdResponseHandlerTest {

    private static final String CORRELATION_ID = "correlation-id-123";
    private static final String PARENT_CORRELATION_ID = "parent-correlation-id-456";

    @InjectMocks
    private AviationSetOperatorIdResponseHandler aviationSetOperatorIdResponseHandler;

    @Mock
    private OperatorIdEventOutcomeService operatorIdEventOutcomeService;

    @Mock
    private AviationSetOperatorIdSendToRegistryProducer registryProducer;

    @Mock
    private OperatorIdErrorNotifierService operatorIdErrorNotifierService;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void handleResponse_whenOutcomeIsSuccess_thenProducesOutcome() {
        final OperatorUpdateEvent event = OperatorUpdateEvent.builder()
                .emitterId("1")
                .operatorId(1L)
                .build();
        final OperatorUpdateEventOutcome expectedOutcome = OperatorUpdateEventOutcome.builder()
                .event(event)
                .build();

        when(operatorIdEventOutcomeService.getAviationOperatorIdEventOutcome(event)).thenReturn(expectedOutcome);

        aviationSetOperatorIdResponseHandler.handleResponse(event, CORRELATION_ID, PARENT_CORRELATION_ID);

        verify(operatorIdEventOutcomeService, times(1)).getAviationOperatorIdEventOutcome(event);
        verify(registryProducer, times(1)).produce(expectedOutcome, CORRELATION_ID, PARENT_CORRELATION_ID);
        verify(operatorIdErrorNotifierService, never()).notifyAuthority(any(NotifyErrorDTO.class));
    }

    @Test
    void handleResponse_whenServiceThrowsException_thenProducesErrorOutcome() {
        final OperatorUpdateEvent event = OperatorUpdateEvent.builder()
                .emitterId("1")
                .operatorId(1L)
                .regulator("EA")
                .build();
        final ArgumentCaptor<OperatorUpdateEventOutcome> outcomeCaptor = ArgumentCaptor.forClass(OperatorUpdateEventOutcome.class);

        when(operatorIdEventOutcomeService.getAviationOperatorIdEventOutcome(event)).thenThrow(new RuntimeException("Service failure"));
        when(accountQueryService.getAccountByEmitterId(any())).thenReturn(Optional.empty());

        aviationSetOperatorIdResponseHandler.handleResponse(event, CORRELATION_ID, PARENT_CORRELATION_ID);

        verify(operatorIdEventOutcomeService, times(1)).getAviationOperatorIdEventOutcome(event);
        verify(registryProducer, times(1)).produce(outcomeCaptor.capture(), eq(CORRELATION_ID), eq(PARENT_CORRELATION_ID));
        verify(operatorIdErrorNotifierService, times(1)).notifyAuthority(any(NotifyErrorDTO.class));

        final OperatorUpdateEventOutcome capturedOutcome = outcomeCaptor.getValue();
        final IntegrationEventErrorDetails expectedError = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0200)
                .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription())
                .build();

        assertThat(capturedOutcome.getEvent()).isEqualTo(event);
        assertThat(capturedOutcome.getErrors()).isEqualTo(List.of(expectedError));
    }
}
