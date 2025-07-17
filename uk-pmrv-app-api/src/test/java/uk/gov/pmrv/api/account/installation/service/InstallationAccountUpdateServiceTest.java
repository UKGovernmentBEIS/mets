package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateFaStatusDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.service.LegalEntityValidationService;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountUpdateServiceTest {

    @InjectMocks
    private InstallationAccountUpdateService service;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;

    @Mock
    private LegalEntityValidationService legalEntityValidationService;

    @Mock
    private InstallationAccountRepository installationAccountRepository;

    @Test
    void updateAccountSiteName() {
        Long accountId = 1L;
        String newSiteName = "newSiteName";

        InstallationAccount account = InstallationAccount.builder().id(accountId).siteName("siteName").build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountSiteName(accountId, newSiteName);

        assertThat(account.getSiteName()).isEqualTo(newSiteName);
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }

    @Test
    void updateAccountRegistryId() {
        Long accountId = 1L;
        Integer newRegistryId = 1234568;

        InstallationAccount account = InstallationAccount.builder().id(accountId).registryId(1234567).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountRegistryId(accountId, newRegistryId);

        assertThat(account.getRegistryId()).isEqualTo(newRegistryId);
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }

    @Test
    void updateAccountSopId() {
        Long accountId = 1L;
        Long newSopId = 1234567892L;

        InstallationAccount account = InstallationAccount.builder().id(accountId).sopId(1234567891L).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountSopId(accountId, newSopId);

        assertThat(account.getSopId()).isEqualTo(newSopId);
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }

    @Test
    void updateAccountAddress() {
        Long accountId = 1L;
        LocationOnShoreDTO address = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("grid").address(AddressDTO.builder()
                .city("city").country("country").line1("line1").line2("line2").postcode("postcode").build()).build();
        LocationOnShore location = LocationOnShore.builder()
                .gridReference("grid")
                .address(Address.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .build())
                .build();
        LocationOnShore newLocation = LocationOnShore.builder()
                .gridReference(address.getGridReference())
                .address(Address.builder()
                        .city(address.getAddress().getCity())
                        .country(address.getAddress().getCountry())
                        .line1(address.getAddress().getLine1())
                        .line2(address.getAddress().getLine2())
                        .postcode(address.getAddress().getPostcode())
                        .build())
                .build();

        InstallationAccount account = InstallationAccount.builder().id(accountId).location(location).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountAddress(accountId, address);

        assertThat(account.getLocation()).isEqualTo(newLocation);
    }

    @Test
    void updateAccountUponPermitGranted() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;

        InstallationAccount account = InstallationAccount.builder().id(accountId).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountUponPermitGranted(accountId, emitterType, BigDecimal.valueOf(40000));

        assertEquals(emitterType, account.getEmitterType());
        assertEquals(installationCategory, account.getInstallationCategory());

        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
        verify(installationAccountStatusService, times(1)).handlePermitGranted(accountId);
    }

    @Test
    void updateAccountUponPermitVariationGranted() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;

        InstallationAccount account = InstallationAccount.builder().id(accountId)
                .emitterType(EmitterType.HSE)
                .installationCategory(InstallationCategory.B)
                .build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountUponPermitVariationGranted(accountId, emitterType, BigDecimal.valueOf(40000));

        assertEquals(EmitterType.HSE, account.getEmitterType());
        assertEquals(installationCategory, account.getInstallationCategory());

        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
        verify(installationAccountStatusService, never()).handlePermitGranted(accountId);
    }

    @Test
    void updateAccountUponPermitVariationRegulatorLedSubmit() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;

        InstallationAccount account = InstallationAccount.builder().id(accountId)
                .emitterType(EmitterType.HSE)
                .installationCategory(InstallationCategory.B)
                .build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountUponPermitVariationRegulatorLedSubmit(accountId, emitterType, BigDecimal.valueOf(40000));

        assertEquals(EmitterType.GHGE, account.getEmitterType());
        assertEquals(installationCategory, account.getInstallationCategory());

        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
        verifyNoInteractions(installationAccountStatusService);
    }

    @Test
    void updateAccountLegalEntity() {
        var accountId = 1L;
        var legalEntityId = 1L;

        var legalEntityDTO = LegalEntityDTO.builder()
                .name("TEST_LE")
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("TEST_LE_REG_NO")
                .address(
                        AddressDTO.builder()
                                .city("TEST_CITY")
                                .country("TEST_COUNTRY")
                                .line1("TEST_L1")
                                .postcode("TEST_POSTCODE")
                                .build()
                )
                .holdingCompany(
                        HoldingCompanyDTO.builder()
                                .name("TEST_HC")
                                .registrationNumber("TEST_HC_REG_NO")
                                .address(
                                        HoldingCompanyAddressDTO.builder()
                                                .city("TEST_CITY")
                                                .line1("TEST_L1")
                                                .postcode("TEST_POSTCODE")
                                                .build()
                                )
                                .build()
                )
                .build();

        var existingLegalEntity = LegalEntity.builder()
                .id(legalEntityId)
                .name("TEST_LE_EX")
                .type(LegalEntityType.PARTNERSHIP)
                .referenceNumber("TEST_LE_REG_NO_EX")
                .location(
                        LocationOnShore.builder()
                                .address(
                                        Address.builder()
                                                .city("TEST_CITY_EX")
                                                .country("TEST_COUNTRY_EX")
                                                .build()
                                )
                                .build()
                )
                .holdingCompany(
                        HoldingCompany.builder()
                                .name("TEST_HC_EX")
                                .build()
                )
                .build();

        var account = InstallationAccount.builder()
                .legalEntity(existingLegalEntity)
                .build();

        when(installationAccountQueryService.getAccountWithLocAndLeWithLocById(accountId)).thenReturn(account);

        service.updateAccountLegalEntity(accountId, legalEntityDTO);

        assertThat(account.getLegalEntity().getName()).isEqualTo("TEST_LE");
        assertThat(account.getLegalEntity().getReferenceNumber()).isEqualTo("TEST_LE_REG_NO");
        assertThat(account.getLegalEntity().getHoldingCompany().getName()).isEqualTo("TEST_HC");
        assertThat(account.getLegalEntity().getType()).isEqualTo(LegalEntityType.LIMITED_COMPANY);
        verify(legalEntityValidationService, times(1))
                .validateNameExistenceInOtherActiveLegalEntities("TEST_LE", legalEntityId);
    }

    @Test
    void validateAndDisableTransferCodeStatus_whenTransferCodeDisabled_thenException() {

        when(installationAccountRepository.findAccountByTransferCodeAndTransferCodeStatus("123456789", TransferCodeStatus.ACTIVE))
                .thenReturn(Optional.empty());

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.disableTransferCodeStatus("123456789"));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }

    @Test
    void validateAndDisableTransferCodeStatus_whenTransferCodeActive_thenOk() {

        final InstallationAccount account = InstallationAccount.builder().transferCodeStatus(TransferCodeStatus.ACTIVE).build();

        when(installationAccountRepository.findAccountByTransferCodeAndTransferCodeStatus("123456789", TransferCodeStatus.ACTIVE))
                .thenReturn(Optional.of(account));

        service.disableTransferCodeStatus("123456789");

        assertEquals(TransferCodeStatus.DISABLED, account.getTransferCodeStatus());
    }

    @Test
    void updateInstallationName() {
        Long accountId = 1L;
        String newInstallationName = "InstallationName";
        InstallationAccount account = InstallationAccount.builder().id(accountId).name("installationName").build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        // Invoke
        service.updateInstallationName(accountId, newInstallationName);

        // Verify
        assertThat(account.getName()).isEqualTo(newInstallationName);
        verify(installationAccountQueryService, times(1)).validateAccountNameExistence(newInstallationName);
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }

    @Test
    void updateAccountUponTransferBGranted() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;

        InstallationAccount account = InstallationAccount.builder().id(accountId)
            .emitterType(EmitterType.HSE)
            .installationCategory(InstallationCategory.B)
            .build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountUponTransferBGranted(accountId, emitterType, BigDecimal.valueOf(40000));

        assertEquals(EmitterType.GHGE, account.getEmitterType());
        assertEquals(installationCategory, account.getInstallationCategory());

        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
        verify(installationAccountStatusService, times(1)).handleTransferBGranted(accountId);
    }

    @Test
    void updateAccountUponTransferAGranted() {

        Long accountId = 1L;

        service.updateAccountUponTransferAGranted(accountId);

        verify(installationAccountStatusService, times(1)).handleTransferAGranted(accountId);
    }

    @Test
    void updateFreeAllocationStatus() {
        Long accountId = 1L;
        AccountUpdateFaStatusDTO updateFreeAllocationStatusDTO = AccountUpdateFaStatusDTO
            .builder()
            .faStatus(true)
            .build();
        InstallationAccount account = InstallationAccount.builder().id(accountId).faStatus(false).build();

        when(installationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateFaStatus(accountId, updateFreeAllocationStatusDTO);

        assertThat(account.isFaStatus()).isEqualTo(updateFreeAllocationStatusDTO.getFaStatus());
        verify(installationAccountQueryService, times(1)).getAccountById(accountId);
    }
}
