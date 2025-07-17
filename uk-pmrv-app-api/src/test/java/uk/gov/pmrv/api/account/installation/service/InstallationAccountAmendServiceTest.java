package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountAmendServiceTest {

    @InjectMocks
    private InstallationAccountAmendService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
	private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationAccountMapper installationAccountMapper;

	@Test
	void amendAccount_non_amendable_fields_changed_should_throw_error() {
	    Long accountId = 1L;
        AppUser appUser = AppUser.builder().build();
		LocationDTO location = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
		
		InstallationAccountDTO previousAccountDTO = InstallationAccountDTO.builder()
                .name("account")
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

        InstallationAccountDTO newAccountDTO = InstallationAccountDTO.builder()
                .name("account")
                .commencementDate(LocalDate.now().minusDays(1))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

		//invoke
		BusinessException businessException = assertThrows(BusinessException.class, () ->
			service.amendAccount(accountId, previousAccountDTO, newAccountDTO, appUser));

		assertThat(businessException.getErrorCode()).isEqualTo(MetsErrorCode.ACCOUNT_FIELD_NOT_AMENDABLE);

		//verify
		verifyNoInteractions(installationAccountQueryService, accountRepository, legalEntityService);
	}
	
	@Test
    void amendAccount_invalid_account_name() {
	    Long accountId = 1L;
        AppUser appUser = AppUser.builder().build();
        LocationDTO location = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
        LocalDate now = LocalDate.now();

        InstallationAccountDTO previousAccountDTO = InstallationAccountDTO.builder()
                .name("account1")
                .commencementDate(now)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

        InstallationAccountDTO newAccountDTO = InstallationAccountDTO.builder()
                .name("account2")
                .commencementDate(now)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        
        doThrow(new BusinessException((MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS))).when(installationAccountQueryService)
            .validateAccountNameExistence(newAccountDTO.getName());

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
        service.amendAccount(accountId, previousAccountDTO, newAccountDTO, appUser));

        assertThat(businessException.getErrorCode()).isEqualTo(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS);
    
        //verify
        verify(installationAccountQueryService, times(1)).validateAccountNameExistence(newAccountDTO.getName());
        verifyNoInteractions(accountRepository, legalEntityService);
    }
	
	@Test
    void amendAccount() {
        Long accountId = 1L;
        AppUser appUser = AppUser.builder().build();
        LocationDTO locationDTO = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
        
        LocationOnShore location = new LocationOnShore();
        location.setId(1L);
        
        LocalDate now = LocalDate.now();
        
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name("le1").build();
        InstallationAccount account = InstallationAccount.builder()
                .id(accountId)
                .name("account")
                .legalEntity(legalEntity)
                .status(InstallationAccountStatus.LIVE)
                .location(location)
                .emitterId("EM00001")
                .build();

        InstallationAccountDTO previousAccountDTO = InstallationAccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le1").build())
                .build();

        InstallationAccountDTO newAccountDTO = InstallationAccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(2L).build())
                .build();
        LegalEntity newLegalEntity = LegalEntity.builder().id(2L).name("le2").build();
        InstallationAccount newAccount = InstallationAccount.builder()
                .id(accountId)
                .name("account")
                .location(location)
                .build();
        InstallationAccount accountSaved = InstallationAccount.builder()
                .name("account")
                .status(InstallationAccountStatus.LIVE)
                .location(location)
                .legalEntity(newLegalEntity)
                .build();
        InstallationAccountDTO accountDTOSaved = InstallationAccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(2L).name("le2").build())
                .build();
        
        when(installationAccountQueryService.getAccountFullInfoById(accountId)).thenReturn(account);
        when(legalEntityService.getLegalEntityById(account.getLegalEntity().getId())).thenReturn(legalEntity);
        when(legalEntityService.resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, appUser)).thenReturn(newLegalEntity);
        when(installationAccountMapper.toInstallationAccount(newAccountDTO, accountId)).thenReturn(newAccount);
        when(accountRepository.save(newAccount)).thenReturn(accountSaved);
        when(installationAccountQueryService.isLegalEntityUnused(legalEntity.getId())).thenReturn(true);
        when(installationAccountMapper.toInstallationAccountDTO(accountSaved)).thenReturn(accountDTOSaved);
        
        //invoke
        InstallationAccountDTO result = service.amendAccount(accountId, previousAccountDTO, newAccountDTO, appUser);
        
        assertThat(result).isEqualTo(accountDTOSaved);

        verify(installationAccountQueryService, times(1)).getAccountFullInfoById(accountId);
        verify(legalEntityService, times(1)).getLegalEntityById(account.getLegalEntity().getId());
        verify(legalEntityService, times(1)).resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, appUser);
        verify(installationAccountMapper, times(1)).toInstallationAccount(newAccountDTO, accountId);
        verify(installationAccountQueryService, times(1)).isLegalEntityUnused(legalEntity.getId());
        verify(installationAccountMapper, times(1)).toInstallationAccountDTO(accountSaved);
        verify(legalEntityService, times(1)).deleteLegalEntity(legalEntity);
        
        ArgumentCaptor<InstallationAccount> accountCaptor = ArgumentCaptor.forClass(InstallationAccount.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        InstallationAccount accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(account.getStatus());
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(newLegalEntity);
    }

}
