package uk.gov.pmrv.api.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountUpdateDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.LocationOnShoreState;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateCommencementDateDTO;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.AddressState;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountUpdateServiceTest {

    @InjectMocks
    private AviationAccountUpdateService aviationAccountUpdateService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;
    
    @Mock
    private AviationAccountStatusService aviationAccountStatusService;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    void updateAviationAccount() {

        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser appUser = AppUser.builder().userId("userId").build();

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);

        String newAccountName = "newAccountName";
        Long newSopId = 2L;
        String newCrcoCode = "newCrcoCode";
        LocationOnShoreStateDTO locationDTO = LocationOnShoreStateDTO.builder()
                .city("city")
                .country("GR")
                .line1("line")
                .postcode("postcode")
                .state("state")
                .build();

        LocationOnShoreState location = LocationOnShoreState.builder()
                .address(AddressState.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .state("state")
                        .build())
                .build();

        AviationAccountUpdateDTO accountUpdateDTO =
                createAccountUpdateDTO(newAccountName, newSopId, newCrcoCode, locationDTO);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService.isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(locationMapper.toLocation(locationDTO)).thenReturn(location);

        aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, appUser);

        assertThat(account.getName()).isEqualTo(newAccountName);
        assertThat(account.getCrcoCode()).isEqualTo(newCrcoCode);
        assertThat(account.getSopId()).isEqualTo(newSopId);
        assertThat(account.getLocation()).isEqualTo(location);
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1)).isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(aviationAccountQueryService, times(1)).isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(locationMapper, times(1)).toLocation(locationDTO);
    }

    @Test
    void updateAviationAccount_with_location() {

        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser appUser = AppUser.builder().userId("userId").build();

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);
        account.setLocation(LocationOnShoreState.builder()
                .address(AddressState.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .state("state")
                        .build())
                .build());

        String newAccountName = "newAccountName";
        Long newSopId = 2L;
        String newCrcoCode = "newCrcoCode";
        LocationOnShoreStateDTO locationDTO = LocationOnShoreStateDTO.builder()
                .city("cityNew")
                .country("GR")
                .line1("lineNew")
                .postcode("postcodeNew")
                .state("stateNew")
                .build();

        LocationOnShoreState location = LocationOnShoreState.builder()
                .address(AddressState.builder()
                        .city("cityNew")
                        .country("GR")
                        .line1("lineNew")
                        .postcode("postcodeNew")
                        .state("stateNew")
                        .build())
                .build();

        AviationAccountUpdateDTO accountUpdateDTO =
                createAccountUpdateDTO(newAccountName, newSopId, newCrcoCode, locationDTO);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService.isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(locationMapper.toLocation(locationDTO)).thenReturn(location);

        aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, appUser);

        assertThat(account.getName()).isEqualTo(newAccountName);
        assertThat(account.getCrcoCode()).isEqualTo(newCrcoCode);
        assertThat(account.getSopId()).isEqualTo(newSopId);
        assertThat(account.getLocation()).isEqualTo(location);
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1)).isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(aviationAccountQueryService, times(1)).isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(locationMapper, times(1)).toLocation(locationDTO);
    }

    @Test
    void updateAviationAccountWithExistingAccountName() {

        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser appUser = AppUser.builder().userId("userId").build();

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);

        String newAccountName = "newAccountName";
        Long newSopId = 2L;
        String newCrcoCode = "newCrcoCode";

        AviationAccountUpdateDTO accountUpdateDTO =
                createAccountUpdateDTO(newAccountName, newSopId, newCrcoCode, null);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService.isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(true);


        BusinessException businessException = assertThrows(BusinessException.class,
                () -> aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, appUser));

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1)).isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(aviationAccountQueryService, times(0)).isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        assertEquals(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, businessException.getErrorCode());
        verify(locationMapper, never()).toLocation(any());
    }


    @Test
    void updateAviationAccount_commencementDate() {
        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022,1,1);

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme, sopId, crcoCode, commencementDate);
        AccountUpdateCommencementDateDTO accountUpdateCommencementDateDTO = AccountUpdateCommencementDateDTO
                .builder()
                .commencementDate(commencementDate).build();

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        aviationAccountUpdateService.updateAccountCommencementDate(accountId,commencementDate);

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);

    }


    @Test
    void updateAviationAccountWithExistingCrcoCode() {

        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser appUser = AppUser.builder().userId("userId").build();

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);

        String newAccountName = "newAccountName";
        Long newSopId = 2L;
        String newCrcoCode = "newCrcoCode";

        AviationAccountUpdateDTO accountUpdateDTO =
                createAccountUpdateDTO(newAccountName, newSopId, newCrcoCode, null);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService.isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(true);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, appUser));

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1)).isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(aviationAccountQueryService, times(1)).isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        assertEquals(MetsErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT, businessException.getErrorCode());
        verify(locationMapper, never()).toLocation(any());
    }

    
    @Test
    void updateAviationAccountWithUKETSAndNoRegistryId() {

        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser appUser = AppUser.builder().userId("userId").build();

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);

        String newAccountName = "newAccountName";
        Long newSopId = 2L;
        String newCrcoCode = "newCrcoCode";

        AviationAccountUpdateDTO accountUpdateDTO =
                createAccountUpdateDTO(newAccountName, newSopId, newCrcoCode, null);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService.isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId)).thenReturn(false);

        aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, appUser);

        assertThat(account.getName()).isEqualTo(newAccountName);
        assertThat(account.getCrcoCode()).isEqualTo(newCrcoCode);
        assertThat(account.getSopId()).isEqualTo(newSopId);
        assertThat(account.getLocation()).isNull();
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1)).isExistingAccountName(newAccountName, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(aviationAccountQueryService, times(1)).isExistingCrcoCode(newCrcoCode, CompetentAuthorityEnum.ENGLAND, emissionTradingScheme, accountId);
        verify(locationMapper, never()).toLocation(any());
    }
    
    @Test
    void closeAviationAccount() {
        Long accountId = 1L;
        String accountName = "accountName";
        String emitterId = "emitterId";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        Long sopId = 1L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.now();
        AppUser user = AppUser
                .builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .userId("userId")
                .firstName("First")
                .lastName("Last")
                .build();
        String reason = "closure reason";

        AviationAccount account = createAccount(accountId, accountName, emitterId, emissionTradingScheme,
                sopId, crcoCode, commencementDate);

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);

        aviationAccountUpdateService.closeAviationAccount(accountId, user, reason);

        assertThat(account.getClosureReason()).isEqualTo(reason);
        assertThat(account.getClosedBy()).isEqualTo("userId");
        assertThat(account.getClosedByName()).isEqualTo("First Last");
        assertThat(account.getClosingDate()).isNotNull();
        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountStatusService, times(1)).handleCloseAccount(accountId);

    }

    @Test
    void updateAccountUponEmpApproved() {
        Long accountId = 1L;
        String newAccountName = "accountName";
        AviationAccount account = AviationAccount.builder()
            .id(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name("name")
            .build();
        LocationOnShoreStateDTO accountContactLocationDTO = createAccountContactLocationDTO();
        LocationOnShoreState accountContactLocation = LocationOnShoreState.builder().address(AddressState.builder().build()).build();

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId))
            .thenReturn(false);
        when(locationMapper.toLocation(accountContactLocationDTO)).thenReturn(accountContactLocation);

        //invoke
        aviationAccountUpdateService.updateAccountUponEmpApproved(accountId, newAccountName, accountContactLocationDTO);

        assertEquals(newAccountName, account.getName());

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId);
        verify(locationMapper, times(1)).toLocation(accountContactLocationDTO);
        verify(aviationAccountStatusService, times(1)).handleEmpApproved(accountId);
    }

    @Test
    void updateAccountUponEmpApproved_business_exception_when_account_name_exists() {
        Long accountId = 1L;
        String newAccountName = "accountName";
        AviationAccount account = AviationAccount.builder()
            .id(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name("name")
            .build();
        LocationOnShoreStateDTO accountContactLocationDTO = createAccountContactLocationDTO();

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId))
            .thenReturn(true);

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> aviationAccountUpdateService.updateAccountUponEmpApproved(accountId, newAccountName, accountContactLocationDTO));

        assertEquals(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, businessException.getErrorCode());

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId);
        verifyNoInteractions(aviationAccountStatusService, locationMapper);
    }
    
    @Test
    void updateAccountUponEmpVariationApproved() {
        Long accountId = 1L;
        String newAccountName = "accountName";
        AviationAccount account = AviationAccount.builder()
            .id(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name("name")
            .build();
        LocationOnShoreStateDTO accountContactLocationDTO = createAccountContactLocationDTO();
        LocationOnShoreState accountContactLocation = LocationOnShoreState.builder().address(AddressState.builder().build()).build();

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId))
            .thenReturn(false);
        when(locationMapper.toLocation(accountContactLocationDTO)).thenReturn(accountContactLocation);

        //invoke
        aviationAccountUpdateService.updateAccountUponEmpVariationApproved(accountId, newAccountName, accountContactLocationDTO);

        assertEquals(newAccountName, account.getName());

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId);
        verify(locationMapper, times(1)).toLocation(accountContactLocationDTO);
    }

    @Test
    void updateAccountUponWorkflowCompleted_business_exception_when_account_name_exists() {
        Long accountId = 1L;
        String newAccountName = "accountName";
        AviationAccount account = AviationAccount.builder()
                .id(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .name("name")
                .build();
        LocationOnShoreStateDTO accountContactLocationDTO = createAccountContactLocationDTO();

        when(aviationAccountQueryService.getAccountById(accountId)).thenReturn(account);
        when(aviationAccountQueryService
                .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId))
                .thenReturn(true);

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> aviationAccountUpdateService.updateAccountUponEmpVariationApproved(accountId, newAccountName, accountContactLocationDTO));

        assertEquals(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, businessException.getErrorCode());

        verify(aviationAccountQueryService, times(1)).getAccountById(accountId);
        verify(aviationAccountQueryService, times(1))
                .isExistingAccountName(newAccountName, account.getCompetentAuthority(), account.getEmissionTradingScheme(), accountId);
        verifyNoInteractions(locationMapper);
    }

	private LocationOnShoreStateDTO createAccountContactLocationDTO() {
		return LocationOnShoreStateDTO.builder()
            .type(LocationType.ONSHORE_STATE)
            .line1("line1")
            .city("city")
            .state("state")
            .country("UK")
            .build();
	}

    private AviationAccount createAccount(Long accountId, String accountName, String emitterId, EmissionTradingScheme emissionTradingScheme,
                                          Long sopId, String crcoCode, LocalDate commencementDate) {
        return AviationAccount.builder()
                .id(accountId)
                .name(accountName)
                .status(AviationAccountStatus.NEW)
                .emitterId(emitterId)
                .accountType(AccountType.AVIATION)
                .emissionTradingScheme(emissionTradingScheme)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(commencementDate)
                .verificationBodyId(1L)
                .sopId(sopId)
                .crcoCode(crcoCode)
                .build();
    }

    private AviationAccountUpdateDTO createAccountUpdateDTO(String name, Long sopId, String crcoCode,
                                                            LocationOnShoreStateDTO location) {
        return AviationAccountUpdateDTO.builder()
                .name(name)
                .sopId(sopId)
                .crcoCode(crcoCode)
                .location(location)
                .build();
    }

}
