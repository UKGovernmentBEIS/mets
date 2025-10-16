package uk.gov.pmrv.api.integration.registry.setoperator.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.SetOperatorIdResponseValidator;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;

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
    private SetOperatorIdResponseValidator setOperatorIdResponseValidator;

    @Test
    void getOperatorIdEventOutcome_whenValidationSucceedsAndAccountExists_setsRegistryIdAndReturnsSuccessOutcome() {
        final String emitterId = "1";
        final int operatorId = 1;
        final SetOperatorIdResponseEvent event = SetOperatorIdResponseEvent.builder()
                .emitterId(emitterId)
                .operatorId(operatorId)
                .build();
        final Account mockAccount = mock(Account.class);

        when(setOperatorIdResponseValidator.validate(event)).thenReturn(Collections.emptyList());
        when(accountRepository.findAccountByEmitterId(emitterId)).thenReturn(Optional.of(mockAccount));

        SetOperatorIdEventOutcome result = service.getOperatorIdEventOutcome(event);

        assertThat(result.getOutcome()).isEqualTo(RegistryResponseStatus.SUCCESS);
        assertThat(result.getErrors()).isEmpty();

        verify(accountRepository, times(1)).findAccountByEmitterId(emitterId);
        verify(mockAccount, times(1)).setRegistryId(operatorId);
        verify(accountRepository, times(1)).save(mockAccount);
    }

    @Test
    void getOperatorIdEventOutcome_whenValidationFails_returnsErrorOutcome() {
        final String emitterId = "2";
        final SetOperatorIdResponseEvent event = SetOperatorIdResponseEvent.builder().emitterId(emitterId).build();
        final List<RegistryIntegrationEventError> validationErrors = List.of(
                RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0201).build()
        );

        when(setOperatorIdResponseValidator.validate(event)).thenReturn(validationErrors);

        SetOperatorIdEventOutcome result = service.getOperatorIdEventOutcome(event);

        assertThat(result.getOutcome()).isEqualTo(RegistryResponseStatus.ERROR);
        assertThat(result.getErrors()).isEqualTo(validationErrors);

        verify(accountRepository, never()).findAccountByEmitterId(emitterId);
        verify(accountRepository, never()).save(org.mockito.Mockito.any(Account.class));
    }

    @Test
    void getOperatorIdEventOutcome_whenValidationSucceedsAndAccountMissing_throwsBusinessException() {
        final String emitterId = "3";
        final SetOperatorIdResponseEvent event = SetOperatorIdResponseEvent.builder().emitterId(emitterId).build();

        when(setOperatorIdResponseValidator.validate(event)).thenReturn(Collections.emptyList());
        when(accountRepository.findAccountByEmitterId(emitterId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.getOperatorIdEventOutcome(event)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountRepository, times(1)).findAccountByEmitterId(emitterId);
        verify(accountRepository, never()).save(org.mockito.Mockito.any(Account.class));
    }
}