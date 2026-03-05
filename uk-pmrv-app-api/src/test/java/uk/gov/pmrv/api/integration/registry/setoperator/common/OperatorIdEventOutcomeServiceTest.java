package uk.gov.pmrv.api.integration.registry.setoperator.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.SetOperatorIdResponseValidator;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.setoperator.aviation.AviationSetOperatorIdExemptStatusUpdateService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorIdEventOutcomeServiceTest {

    @InjectMocks
    private OperatorIdEventOutcomeService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private AviationSetOperatorIdExemptStatusUpdateService aviationSetOperatorIdExemptStatusUpdateService;

    @Mock
    private SetOperatorIdResponseValidator setOperatorIdResponseValidator;

    @Test
    void getAviationOperatorIdEventOutcome_whenValidationSucceedsAndAccountExists_setsRegistryIdAndReturnsSuccessOutcome() {
        final String emitterId = "1";
        final Long operatorId = 1L;
        final OperatorUpdateEvent event = OperatorUpdateEvent.builder()
                .emitterId(emitterId)
                .operatorId(operatorId)
                .build();
        final Account mockAccount = mock(Account.class);

        when(setOperatorIdResponseValidator.validateAviation(event)).thenReturn(Collections.emptyList());
        when(accountRepository.findAccountByEmitterId(emitterId)).thenReturn(Optional.of(mockAccount));


        OperatorUpdateEventOutcome result = service.getAviationOperatorIdEventOutcome(event);

        assertThat(result.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(result.getErrors()).isEmpty();

        ArgumentCaptor<AviationAccountUpdatedRegistryEvent> eventCaptor =
                ArgumentCaptor.forClass(AviationAccountUpdatedRegistryEvent.class);
        verify(publisher).publishEvent(eventCaptor.capture());
        verify(aviationSetOperatorIdExemptStatusUpdateService, times(1))
                .notifyRegistryWithExemptStatuses(mockAccount);
        verify(accountRepository, times(1)).findAccountByEmitterId(emitterId);
        verify(mockAccount, times(1)).setRegistryId(operatorId.intValue());
        verify(accountRepository, times(1)).save(mockAccount);
    }

    @Test
    void getAviationOperatorIdEventOutcome_whenValidationFails_returnsErrorOutcome() {
        final String emitterId = "2";
        final OperatorUpdateEvent event = OperatorUpdateEvent.builder().emitterId(emitterId).build();
        final List<IntegrationEventErrorDetails> validationErrors = List.of(
                IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0201).build()
        );

        when(setOperatorIdResponseValidator.validateAviation(event)).thenReturn(validationErrors);

        OperatorUpdateEventOutcome result = service.getAviationOperatorIdEventOutcome(event);

        assertThat(result.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(result.getErrors()).isEqualTo(validationErrors);

        verify(accountRepository, never()).findAccountByEmitterId(emitterId);
        verify(accountRepository, never()).save(org.mockito.Mockito.any(Account.class));
    }

    @Test
    void getInstallationOperatorIdEventOutcome_whenValidationSucceedsAndAccountMissing_throwsBusinessException() {
        final String emitterId = "3";
        final OperatorUpdateEvent event = OperatorUpdateEvent.builder().emitterId(emitterId).build();

        when(setOperatorIdResponseValidator.validateInstallation(event)).thenReturn(Collections.emptyList());
        when(accountRepository.findAccountByEmitterId(emitterId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.getInstallationOperatorIdEventOutcome(event)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountRepository, times(1)).findAccountByEmitterId(emitterId);
        verify(accountRepository, never()).save(org.mockito.Mockito.any(Account.class));
    }
}