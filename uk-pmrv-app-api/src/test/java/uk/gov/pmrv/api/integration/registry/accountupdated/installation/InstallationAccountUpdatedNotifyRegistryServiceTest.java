package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountupdated.common.RegistryIntegrationEmailNotifierService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedAddRequestActionService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryProducer;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountUpdatedNotifyRegistryServiceTest {

    @Mock
    private InstallationAccountQueryOrchestrator accountQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private InstallationAccountUpdatedRegistryProducer registryProducer;

    @Mock
    private InstallationAccountUpdatedAddRequestActionService addRequestActionService;

    @Mock
    private RegistryIntegrationEmailNotifierService registryIntegrationEmailNotifierService;

    @InjectMocks
    private InstallationAccountUpdatedNotifyRegistryService installationAccountUpdatedNotifyRegistryService;

    @Test
    void notifyRegistry() {

        InstallationAccountUpdatedRegistryEvent event = buildInstallationAccountUpdatedEvent();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO();
        PermitContainer permitContainer = buildPermitContainer();

        when(accountQueryService.getAccountWithPermit(1L)).thenReturn(installationAccountPermitDTO);
        when(permitQueryService.getPermitContainerByAccountId(1L)).thenReturn(permitContainer);

        installationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(accountQueryService).getAccountWithPermit(1L);
        verify(permitQueryService).getPermitContainerByAccountId(1L);
        verify(registryProducer).produce(any(AccountUpdatingEvent.class));
        verify(addRequestActionService).addRequestAction(eq("requestId"), eq(installationAccountPermitDTO), eq(permitContainer));

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent data = captor.getValue();
        assertNotNull(data);
    }

    @Test
    void notifyRegistry_registry_id_empty() {

        InstallationAccountUpdatedRegistryEvent event = buildInstallationAccountUpdatedEvent();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO();
        installationAccountPermitDTO.getAccount().setRegistryId(null);
        PermitContainer permitContainer = buildPermitContainer();

        when(accountQueryService.getAccountWithPermit(1L)).thenReturn(installationAccountPermitDTO);
        when(permitQueryService.getPermitContainerByAccountId(1L)).thenReturn(permitContainer);

        installationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(accountQueryService).getAccountWithPermit(1L);
        verify(permitQueryService).getPermitContainerByAccountId(1L);
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(addRequestActionService);
    }

    @Test
    void notifyRegistry_when_requestId_empty() {

        InstallationAccountUpdatedRegistryEvent event = InstallationAccountUpdatedRegistryEvent.builder().accountId(1L)
                .isFromSetOperatorId(true).build();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO();
        PermitContainer permitContainer = buildPermitContainer();

        when(accountQueryService.getAccountWithPermit(1L)).thenReturn(installationAccountPermitDTO);
        when(permitQueryService.getPermitContainerByAccountId(1L)).thenReturn(permitContainer);

        installationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(accountQueryService).getAccountWithPermit(1L);
        verify(permitQueryService).getPermitContainerByAccountId(1L);
        verify(registryProducer).produce(any(AccountUpdatingEvent.class));
        verifyNoInteractions(addRequestActionService);
    }

    @Test
    void buildAccountUpdatedPayload_limited_company_with_reference_number() {

        InstallationAccountPermitDTO accountPermitDTO = buildInstallationAccountPermitDTO();
        PermitContainer permitContainer = buildPermitContainer();

        AccountUpdatingEvent result = installationAccountUpdatedNotifyRegistryService.buildAccountUpdatedPayload(accountPermitDTO, permitContainer);

        assertNotNull(result);
        assertNotNull(result.getAccountDetails());
        assertNotNull(result.getAccountHolder());

        UpdateAccountDetailsMessage accountDetails = result.getAccountDetails();
        assertEquals("Installation Name", accountDetails.getInstallationName());
        assertEquals("OPERATOR_HOLDING_ACCOUNT", accountDetails.getAccountType());
        assertEquals("Operator Name", accountDetails.getAccountName());
        assertEquals("permitId", accountDetails.getPermitId());
        assertEquals("123", accountDetails.getRegistryId());
        assertEquals(2023, accountDetails.getFirstYearOfVerifiedEmissions());
        assertEquals(1, accountDetails.getInstallationActivityTypes().size());

        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("ORGANISATION", accountHolder.getAccountHolderType());
        assertEquals("entityName", accountHolder.getName());
        assertEquals("address1", accountHolder.getAddressLine1());
        assertEquals("city1", accountHolder.getTownOrCity());
        assertEquals("12345", accountHolder.getPostalCode());
        assertEquals("GR", accountHolder.getCountry());
        assertFalse(accountHolder.getCrnNotExist());
        assertEquals("companyRef123", accountHolder.getCompanyRegistrationNumber());
        assertNull(accountHolder.getCrnJustification());
    }

    @Test
    void buildAccountUpdatedPayload_limited_company_without_reference_number() {

        InstallationAccountPermitDTO accountPermitDTO = buildInstallationAccountPermitDTO();
        accountPermitDTO.getAccount().getLegalEntity().setReferenceNumber(null);
        accountPermitDTO.getAccount().getLegalEntity().setNoReferenceNumberReason("No reason provided");
        PermitContainer permitContainer = buildPermitContainer();
        permitContainer.getInstallationOperatorDetails().setCompanyReferenceNumber(null);

        AccountUpdatingEvent result = installationAccountUpdatedNotifyRegistryService.buildAccountUpdatedPayload(accountPermitDTO, permitContainer);

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertTrue(accountHolder.getCrnNotExist());
        assertNull(accountHolder.getCompanyRegistrationNumber());
        assertEquals("No reason provided", accountHolder.getCrnJustification());
    }

    @Test
    void buildAccountUpdatedPayload_sole_trader() {

        InstallationAccountPermitDTO accountPermitDTO = buildInstallationAccountPermitDTO();
        accountPermitDTO.getAccount().getLegalEntity().setType(LegalEntityType.SOLE_TRADER);
        PermitContainer permitContainer = buildPermitContainer();
        permitContainer.getInstallationOperatorDetails().setOperatorType(LegalEntityType.SOLE_TRADER);

        AccountUpdatingEvent result = installationAccountUpdatedNotifyRegistryService.buildAccountUpdatedPayload(accountPermitDTO, permitContainer);

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("INDIVIDUAL", accountHolder.getAccountHolderType());
        assertNull(accountHolder.getCompanyRegistrationNumber());
        assertNull(accountHolder.getCrnJustification());
    }

    @Test
    void buildAccountUpdatedPayload_gb_country_code_replaced() {

        InstallationAccountPermitDTO accountPermitDTO = buildInstallationAccountPermitDTO();
        PermitContainer permitContainer = buildPermitContainer();
        permitContainer.getInstallationOperatorDetails().getOperatorDetailsAddress().setCountry("GB");

        AccountUpdatingEvent result = installationAccountUpdatedNotifyRegistryService.buildAccountUpdatedPayload(accountPermitDTO, permitContainer);

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("UK", accountHolder.getCountry());
    }

    private InstallationAccountUpdatedRegistryEvent buildInstallationAccountUpdatedEvent() {
        return InstallationAccountUpdatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("requestId")
                .build();
    }

    private InstallationAccountPermitDTO buildInstallationAccountPermitDTO() {
        InstallationAccountDTO account = InstallationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.INSTALLATION)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .name("accountName")
                .emitterId("emitterId")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.now())
                .registryId(123)
                .registryReportingFirstYear(2023)
                .legalEntity(LegalEntityDTO.builder()
                        .name("entityName")
                        .type(LegalEntityType.LIMITED_COMPANY)
                        .referenceNumber("entityRef123")
                        .address(AddressDTO.builder()
                                .line1("address1")
                                .city("city1")
                                .country("GR")
                                .postcode("12345")
                                .build())
                        .build())
                .location(LocationDTO.builder().type(LocationType.ONSHORE).build())
                .acceptedDate(LocalDateTime.now())
                .status(InstallationAccountStatus.LIVE)
                .build();

        PermitDetailsDTO detailsDTO = PermitDetailsDTO.builder()
                .id("permitId")
                .activationDate(LocalDate.now())
                .regulatedActivities(RegulatedActivities.builder()
                        .regulatedActivities(List.of(
                                RegulatedActivity.builder()
                                        .type(RegulatedActivityType.COMBUSTION)
                                        .build()))
                        .build())
                .build();

        return InstallationAccountPermitDTO.builder()
                .account(account)
                .permit(detailsDTO)
                .build();
    }

    private PermitContainer buildPermitContainer() {
        InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName("Installation Name")
                .operator("Operator Name")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("companyRef123")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("address1")
                        .city("city1")
                        .country("GR")
                        .postcode("12345")
                        .build())
                .build();

        return PermitContainer.builder()
                .permit(Permit.builder().build())
                .permitType(PermitType.GHGE)
                .installationOperatorDetails(operatorDetails)
                .build();
    }
}