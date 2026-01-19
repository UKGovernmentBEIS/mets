package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityDeleteValidator;
import uk.gov.pmrv.api.account.domain.event.AccountContactRegistryEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmrvOperatorAuthorityDeletionServiceTest {

    private static final String USER_ID = "1";
    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private List<OperatorAuthorityDeleteValidator> operatorAuthorityDeleteValidators;

    @Mock
    private ApplicationEventPublisher baseEventPublisher;

    @Mock
    private ApplicationEventPublisher childEventPublisher;

    private PmrvOperatorAuthorityDeletionService pmrvOperatorAuthorityDeletionService;

    @BeforeEach
    void setUp() {
        pmrvOperatorAuthorityDeletionService = new PmrvOperatorAuthorityDeletionService(
                authorityRepository,
                operatorAuthorityDeleteValidators,
                baseEventPublisher,
                childEventPublisher
        );
    }

    @Test
    void deleteAccountOperatorAuthorityAndNotifyRegistry_publishes_event_after_successful_base_deletion() {
        Authority authority = buildAuthority();

        when(authorityRepository.findByUserId(USER_ID)).thenReturn(List.of(authority));

        pmrvOperatorAuthorityDeletionService.deleteAccountOperatorAuthorityAndNotifyRegistry(USER_ID, ACCOUNT_ID);

        verify(authorityRepository).findByUserId(USER_ID);
        verify(authorityRepository).delete(authority);

        verify(operatorAuthorityDeleteValidators).forEach(any());

        ArgumentCaptor<OperatorAuthorityDeletionEvent> baseEventCaptor = ArgumentCaptor.forClass(OperatorAuthorityDeletionEvent.class);
        verify(baseEventPublisher).publishEvent(baseEventCaptor.capture());
        assertEquals(ACCOUNT_ID, baseEventCaptor.getValue().getAccountId());

        ArgumentCaptor<AccountContactRegistryEvent> childEventCaptor = ArgumentCaptor.forClass(AccountContactRegistryEvent.class);
        verify(childEventPublisher).publishEvent(childEventCaptor.capture());
        AccountContactRegistryEvent publishedEvent = childEventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(ACCOUNT_ID, publishedEvent.getAccountId());

        verifyNoMoreInteractions(childEventPublisher);
    }

    private Authority buildAuthority() {
        return Authority.builder()
                .userId(USER_ID)
                .accountId(ACCOUNT_ID)
                .build();
    }
}