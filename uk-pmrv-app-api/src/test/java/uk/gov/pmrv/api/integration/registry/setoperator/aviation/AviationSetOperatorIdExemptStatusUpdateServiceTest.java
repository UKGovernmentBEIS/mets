package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptFlagEvent;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationSetOperatorIdExemptStatusUpdateServiceTest {

    @Mock
    private AviationAccountReportingStatusRepository aviationAccountReportingStatusRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AviationSetOperatorIdExemptStatusUpdateService service;

    @Test
    void notifyRegistryWithExemptStatuses() {
        Long accountId = 1L;
        Integer registryId = 123;
        AviationAccount account = AviationAccount.builder()
                .id(accountId)
                .registryId(registryId)
                .build();

        AviationAccountReportingStatus statusRequired = AviationAccountReportingStatus.builder()
                .year(Year.of(2023))
                .status(AviationAccountReportingStatusType.REQUIRED_TO_REPORT)
                .build();

        AviationAccountReportingStatus statusExempt = AviationAccountReportingStatus.builder()
                .year(Year.of(2022))
                .status(AviationAccountReportingStatusType.EXEMPT_NON_COMMERCIAL)
                .build();

        when(aviationAccountReportingStatusRepository.findByAccountIdOrderByYearDesc(accountId))
                .thenReturn(List.of(statusRequired, statusExempt));

        service.notifyRegistryWithExemptStatuses(account);

        ArgumentCaptor<AviationAccountExemptFlagEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountExemptFlagEvent.class);
        verify(publisher, times(2)).publishEvent(eventCaptor.capture());

        List<AviationAccountExemptFlagEvent> publishedEvents = eventCaptor.getAllValues();

        AviationAccountExemptFlagEvent event1 = publishedEvents.get(0);
        assertEquals(Year.of(2023), event1.getYear());
        assertEquals(registryId, event1.getRegistryId());
        assertEquals(accountId, event1.getAccountId());
        assertFalse(event1.isExempt());

        AviationAccountExemptFlagEvent event2 = publishedEvents.get(1);
        assertEquals(Year.of(2022), event2.getYear());
        assertEquals(registryId, event2.getRegistryId());
        assertEquals(accountId, event2.getAccountId());
        assertTrue(event2.isExempt());

        verify(aviationAccountReportingStatusRepository, times(1)).findByAccountIdOrderByYearDesc(accountId);
    }
}