package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.requestaction.WithholdFlagRegistryIntegrationAddRequestActionService;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountWithholdFlagNotifyRegistryServiceTest {

    @Mock
    private InstallationAccountQueryService accountQueryService;

    @Mock
    private InstallationWithholdFlagRegistryProducer registryProducer;

    @Mock
    private WithholdFlagRegistryIntegrationAddRequestActionService addRequestActionService;

    @Mock
    private InstallationWithholdFlagEmailNotifierService emailNotifierService;

    @InjectMocks
    private InstallationWithholdFlagNotifyRegistryService service;

    @Test
    void notifyRegistry_success() {
        Long accountId = 1L;
        int registryId = 12345;
        int year = 2026;
        boolean withholdFlag = true;
        String requestId = "req-123";

        WithholdFlagRegistryEvent event = WithholdFlagRegistryEvent.builder()
                .accountId(accountId)
                .year(year)
                .withholdFlag(withholdFlag)
                .requestId(requestId)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emitterType(EmitterType.GHGE)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        ArgumentCaptor<AccountWithholdUpdateEvent> captor = ArgumentCaptor.forClass(AccountWithholdUpdateEvent.class);
        verify(registryProducer).produce(captor.capture());

        AccountWithholdUpdateEvent producedEvent = captor.getValue();
        assertEquals(Long.valueOf(registryId), producedEvent.getRegistryId());
        assertEquals(Year.of(year), producedEvent.getReportingYear());
        assertEquals(withholdFlag, producedEvent.getWithholdFlag());
        verify(addRequestActionService).addRequestAction(eq(requestId), eq(producedEvent));
    }

    @Test
    void notifyRegistry_aborts_when_registry_id_missing() {
        Long accountId = 1L;
        int year = 2026;
        boolean withholdFlag = true;
        String requestId = "req-123";

        WithholdFlagRegistryEvent event = WithholdFlagRegistryEvent.builder()
                .accountId(accountId)
                .year(year)
                .withholdFlag(withholdFlag)
                .requestId(requestId)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(null)
                .emitterId(EmitterType.GHGE.name())
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verify(emailNotifierService).registryIdNonExistenceNotifyRegulator(accountDTO);
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(addRequestActionService);
    }
}