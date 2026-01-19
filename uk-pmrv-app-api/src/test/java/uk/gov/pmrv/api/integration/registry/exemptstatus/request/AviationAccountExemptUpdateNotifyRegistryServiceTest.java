package uk.gov.pmrv.api.integration.registry.exemptstatus.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptFlagEvent;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptUpdateNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptUpdateRegistryProducer;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountExemptUpdateNotifyRegistryServiceTest {

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AviationAccountExemptUpdateRegistryProducer producer;

    @InjectMocks
    private AviationAccountExemptUpdateNotifyRegistryService service;

    @Test
    void notifyRegistry() {
        Long accountId = 1L;
        Integer registryId = 123;
        Year year = Year.of(2023);
        boolean isExempt = true;

        AviationAccountExemptFlagEvent event = AviationAccountExemptFlagEvent.builder()
                .accountId(accountId)
                .registryId(registryId)
                .year(year)
                .isExempt(isExempt)
                .build();

        when(aviationAccountQueryService.registryIdExistsForAccount(accountId)).thenReturn(true);

        service.notifyRegistry(event);

        verify(aviationAccountQueryService).registryIdExistsForAccount(accountId);

        ArgumentCaptor<AccountExemptionUpdateEvent> captor = ArgumentCaptor.forClass(AccountExemptionUpdateEvent.class);
        verify(producer).produce(captor.capture());

        AccountExemptionUpdateEvent result = captor.getValue();
        assertNotNull(result);
        assertEquals(isExempt, result.getExemptionFlag());
        assertEquals(registryId.longValue(), result.getRegistryId());
        assertEquals(year, result.getReportingYear());
    }

    @Test
    void notifyRegistry_when_registry_id_not_exists() {
        Long accountId = 1L;
        AviationAccountExemptFlagEvent event = AviationAccountExemptFlagEvent.builder()
                .accountId(accountId)
                .build();

        when(aviationAccountQueryService.registryIdExistsForAccount(accountId)).thenReturn(false);

        service.notifyRegistry(event);

        verify(aviationAccountQueryService).registryIdExistsForAccount(accountId);
        verifyNoInteractions(producer);
    }
}