package uk.gov.pmrv.api.integration.registry.accountcreated.installation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedSendToRegistryProducer;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceRegistryIntegrationAddRequestActionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class InstallationAccountCreatedNotifyRegistryServiceTest {

    @Mock
    private InstallationAccountQueryOrchestrator accountQueryService;

    @Mock
    private InstallationAccountCreatedSendToRegistryProducer registryProducer;

    @Mock
    private PermitIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;

    @InjectMocks
    private InstallationAccountCreatedNotifyRegistryService installationAccountCreatedNotifyRegistryService;

    @Test
    void notifyRegistry() {

        InstallationAccountCreatedRegistryEvent event = buildInstallationAccountCreatedEvent();
        when(accountQueryService.getAccountWithPermit(1L)).thenReturn(buildInstallationAccountPermitDTO());

        installationAccountCreatedNotifyRegistryService.notifyRegistry(event);

        verify(registryProducer).produce(any(AccountOpeningEvent.class));
        verify(accountQueryService).getAccountWithPermit(1L);
        verify(addRequestActionService).addRequestAction(eq("requestId"), any(InstallationAccountCreatedRequestActionDTO.class));

        ArgumentCaptor<AccountOpeningEvent> captor =
                ArgumentCaptor.forClass(AccountOpeningEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountOpeningEvent data = captor.getValue();
        assertNotNull(data);
    }

    @Test
    void notifyRegistry_registry_id_exists() {

        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO();
        installationAccountPermitDTO.getAccount().setRegistryId(1);

        when(accountQueryService.getAccountWithPermit(1L)).thenReturn(installationAccountPermitDTO);

        installationAccountCreatedNotifyRegistryService.notifyRegistry(buildInstallationAccountCreatedEvent());

        verify(accountQueryService).getAccountWithPermit(1L);
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(addRequestActionService);

    }


    private InstallationAccountCreatedRegistryEvent buildInstallationAccountCreatedEvent() {
        return InstallationAccountCreatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("requestId")
                .build();
    }

    private InstallationAccountPermitDTO buildInstallationAccountPermitDTO() {
        InstallationAccountDTO account = InstallationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.INSTALLATION)
                .name("accountName")
                .emitterId("emitterId")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.now())
                .legalEntity(LegalEntityDTO.builder().name("entityName").type(LegalEntityType.LIMITED_COMPANY)
                        .referenceNumber("1").address(AddressDTO.builder().line1("adress1")
                                .city("city1").country("GR").postcode("12345").build()).build())
                .location(LocationDTO.builder().type(LocationType.ONSHORE).build())
                .acceptedDate(LocalDateTime.now())
                .status(InstallationAccountStatus.LIVE)
                .build();

        PermitDetailsDTO detailsDTO = PermitDetailsDTO.builder()
                .id("permitId")
                .activationDate(LocalDate.now())
                .regulatedActivities(RegulatedActivities.builder()
                        .regulatedActivities(List.of(RegulatedActivity.builder().type(RegulatedActivityType.WASTE).build())).build())
                .build();

        return InstallationAccountPermitDTO.builder().account(account).permit(detailsDTO).build();
    }


}
