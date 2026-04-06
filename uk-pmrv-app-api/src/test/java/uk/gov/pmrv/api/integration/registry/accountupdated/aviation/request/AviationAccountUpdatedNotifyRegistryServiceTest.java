package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

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
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction.AviationAccountUpdatedRequestActionService;
import uk.gov.pmrv.api.integration.registry.accountupdated.common.RegistryIntegrationEmailNotifierService;

import java.time.LocalDate;
import java.util.Optional;

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
public class AviationAccountUpdatedNotifyRegistryServiceTest {

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AviationAccountUpdatedRegistryProducer registryProducer;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;

    @Mock
    private AviationAccountUpdatedRequestActionService requestActionService;

    @Mock
    private RegistryIntegrationEmailNotifierService registryIntegrationEmailNotifierService;

    @InjectMocks
    private AviationAccountUpdatedNotifyRegistryService aviationAccountUpdatedNotifyRegistryService;

    @Test
    void notifyRegistry() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildLimitedCompanyOrganisation());

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(aviationAccountQueryService).getAviationAccountDTOById(1L);
        verify(empQueryService).getEmpIdByAccountId(1L);
        verify(empQueryService).getEmpContainerById("empId");
        verify(registryProducer).produce(any(AccountUpdatingEvent.class));
        verify(requestActionService).addRequestAction(eq("requestId"), eq(accountDTO), eq(empContainer.getEmissionsMonitoringPlan()));

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent data = captor.getValue();
        assertNotNull(data);
        assertNotNull(data.getAccountDetails());
        assertNotNull(data.getAccountHolder());
    }

    @Test
    void notifyRegistry_monitoring_plan_id_empty() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.empty());

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(aviationAccountQueryService).getAviationAccountDTOById(1L);
        verify(empQueryService).getEmpIdByAccountId(1L);
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(requestActionService);
    }

    @Test
    void notifyRegistry_registry_id_empty() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        accountDTO.setRegistryId(null);

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(EmissionsMonitoringPlanUkEtsContainer.builder().build());

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(aviationAccountQueryService).getAviationAccountDTOById(1L);
        verify(empQueryService).getEmpIdByAccountId(1L);
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(requestActionService);
    }

    @Test
    void notifyRegistry_request_id_empty() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        event.setRequestId(null);
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildLimitedCompanyOrganisation());

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        verify(aviationAccountQueryService).getAviationAccountDTOById(1L);
        verify(empQueryService).getEmpIdByAccountId(1L);
        verify(empQueryService).getEmpContainerById("empId");
        verify(registryProducer).produce(any(AccountUpdatingEvent.class));

    }

    @Test
    void notifyRegistry_limited_company() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildLimitedCompanyOrganisation());

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent result = captor.getValue();

        assertNotNull(result);
        assertNotNull(result.getAccountDetails());
        assertNotNull(result.getAccountHolder());

        UpdateAccountDetailsMessage accountDetails = result.getAccountDetails();
        assertEquals("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT", accountDetails.getAccountType());
        assertEquals("123", accountDetails.getRegistryId());
        assertEquals("empId", accountDetails.getMonitoringPlanId());
        assertEquals(2023, accountDetails.getFirstYearOfVerifiedEmissions());

        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("ORGANISATION", accountHolder.getAccountHolderType());
        assertEquals("emp container operator name", accountHolder.getName());
        assertFalse(accountHolder.getCrnNotExist());
        assertNull(accountHolder.getCrnJustification());
        assertEquals("REG123456", accountHolder.getCompanyRegistrationNumber());
        assertEquals("address1", accountHolder.getAddressLine1());
        assertEquals("address2", accountHolder.getAddressLine2());
        assertEquals("city1", accountHolder.getTownOrCity());
        assertEquals("state1", accountHolder.getStateOrProvince());
        assertEquals("12345", accountHolder.getPostalCode());
        assertEquals("GR", accountHolder.getCountry());
    }

    @Test
    void notifyRegistry_individual() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildIndividualOrganisation());

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent result = captor.getValue();

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("INDIVIDUAL", accountHolder.getAccountHolderType());
        assertNull(accountHolder.getCrnNotExist());
        assertNull(accountHolder.getCrnJustification());
        assertNull(accountHolder.getCompanyRegistrationNumber());
    }

    @Test
    void notifyRegistry_partnership() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildPartnershipOrganisation());

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent result = captor.getValue();

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("ORGANISATION", accountHolder.getAccountHolderType());
        assertEquals("emp container operator name", accountHolder.getName());
        assertTrue(accountHolder.getCrnNotExist());
        assertEquals("Partnership", accountHolder.getCrnJustification());
        assertNull(accountHolder.getCompanyRegistrationNumber());
    }

    @Test
    void notifyRegistry_gb_country_code_replaced() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        LimitedCompanyOrganisation organisation = buildLimitedCompanyOrganisation();
        organisation.getOrganisationLocation().setCountry("GB");
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(organisation);

        when(aviationAccountQueryService.getAviationAccountDTOById(1L)).thenReturn(accountDTO);
        when(empQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));
        when(empQueryService.getEmpContainerById("empId")).thenReturn(empContainer);

        aviationAccountUpdatedNotifyRegistryService.notifyRegistry(event);

        ArgumentCaptor<AccountUpdatingEvent> captor = ArgumentCaptor.forClass(AccountUpdatingEvent.class);
        verify(registryProducer).produce(captor.capture());
        AccountUpdatingEvent result = captor.getValue();

        assertNotNull(result);
        AccountHolderMessage accountHolder = result.getAccountHolder();
        assertEquals("UK", accountHolder.getCountry());
    }

    private AviationAccountUpdatedRegistryEvent buildAviationAccountUpdatedRegistryEvent() {
        return AviationAccountUpdatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("requestId")
                .build();
    }

    private AviationAccountDTO buildAviationAccountDTO() {
        return AviationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.AVIATION)
                .name("Account Name")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.of(2023, 1, 1))
                .registryId(123)
                .build();
    }

    private EmissionsMonitoringPlanUkEtsContainer buildEmissionsMonitoringPlanUkEtsContainer(uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure organisationStructure) {
        EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder()
                .operatorName("emp container operator name")
                .organisationStructure(organisationStructure)
                .build();

        EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(operatorDetails)
                .build();

        return EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(emp)
                .build();
    }

    private LimitedCompanyOrganisation buildLimitedCompanyOrganisation() {
        return LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("REG123456")
                .organisationLocation(buildLocationOnShoreStateDTO("GR"))
                .build();
    }

    private IndividualOrganisation buildIndividualOrganisation() {
        return IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("John Doe")
                .organisationLocation(buildLocationOnShoreStateDTO("GR"))
                .build();
    }

    private PartnershipOrganisation buildPartnershipOrganisation() {
        return PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("Partnership Name")
                .organisationLocation(buildLocationOnShoreStateDTO("GR"))
                .build();
    }

    private LocationOnShoreStateDTO buildLocationOnShoreStateDTO(String country) {
        return LocationOnShoreStateDTO.builder()
                .line1("address1")
                .line2("address2")
                .city("city1")
                .state("state1")
                .postcode("12345")
                .country(country)
                .build();
    }
}