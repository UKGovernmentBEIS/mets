package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.NotifyErrorDTO;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdErrorNotifierService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.OperatorIdEventOutcomeService;
import uk.gov.pmrv.api.integration.registry.setoperator.common.RegistryIntegrationEventError;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdEventOutcome;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdResponseEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationSetOperatorIdResponseHandlerTest {

    @InjectMocks
    private InstallationSetOperatorIdResponseHandler installationSetOperatorIdResponseHandler;

    @Mock
    private OperatorIdEventOutcomeService operatorIdEventOutcomeService;

    @Mock
    private InstallationSetOperatorIdSendToRegistryProducer registryProducer;

    @Mock
    private OperatorIdErrorNotifierService operatorIdErrorNotifierService;

    @Test
    void handleResponse_whenOutcomeIsSuccess_thenProducesOutcome() {
        final String correlationId = "1";
        final SetOperatorIdResponseEvent event = SetOperatorIdResponseEvent.builder()
                .emitterId("1")
                .operatorId(1)
                .build();
        final SetOperatorIdEventOutcome expectedOutcome = SetOperatorIdEventOutcome.builder()
                .event(event)
                .build();

        when(operatorIdEventOutcomeService.getOperatorIdEventOutcome(event)).thenReturn(expectedOutcome);

        installationSetOperatorIdResponseHandler.handleResponse(event, correlationId);

        verify(operatorIdEventOutcomeService, times(1)).getOperatorIdEventOutcome(event);
        verify(registryProducer, times(1)).produce(expectedOutcome);
        verify(operatorIdErrorNotifierService, never()).notifyAuthority(any(NotifyErrorDTO.class));

    }

    @Test
    void handleResponse_whenServiceThrowsException_thenProducesErrorOutcome() {
        final String correlationId = "1";
        final SetOperatorIdResponseEvent event = SetOperatorIdResponseEvent.builder()
                .emitterId("2")
                .operatorId(1)
                .build();
        final ArgumentCaptor<SetOperatorIdEventOutcome> outcomeCaptor = ArgumentCaptor.forClass(SetOperatorIdEventOutcome.class);

        when(operatorIdEventOutcomeService.getOperatorIdEventOutcome(event)).thenThrow(new RuntimeException("Service failure"));

        installationSetOperatorIdResponseHandler.handleResponse(event, correlationId);

        verify(operatorIdEventOutcomeService, times(1)).getOperatorIdEventOutcome(event);
        verify(registryProducer, times(1)).produce(outcomeCaptor.capture());
        verify(operatorIdErrorNotifierService, times(1)).notifyAuthority(any(NotifyErrorDTO.class));


        final SetOperatorIdEventOutcome capturedOutcome = outcomeCaptor.getValue();
        final RegistryIntegrationEventError expectedError = RegistryIntegrationEventError.builder()
                .error(RegistryResponseErrorCode.ERROR_0200)
                .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription())
                .build();

        assertThat(capturedOutcome.getEvent()).isEqualTo(event);
        assertThat(capturedOutcome.getErrors()).isEqualTo(List.of(expectedError));
    }
}