package uk.gov.pmrv.api.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.service.VerifierAccountAccessByAccountTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountQueryServiceTest {

    @InjectMocks
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private AviationAccountMapper aviationAccountMapper;
    
    @Mock
    private VerifierAccountAccessByAccountTypeService verifierAccountAccessService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isExistingAccountName(boolean exists) {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;

        when(aviationAccountRepository
            .existsByNameAndCompetentAuthorityAndEmissionTradingScheme(accountName, competentAuthority, emissionTradingScheme))
            .thenReturn(exists);

        assertEquals(exists, aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, null));

        verify(aviationAccountRepository, times(1))
            .existsByNameAndCompetentAuthorityAndEmissionTradingScheme(accountName, competentAuthority, emissionTradingScheme);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isExistingCrcoCode(boolean exists) {
        String code = "code";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;

        when(aviationAccountRepository
            .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingScheme(code, competentAuthority, emissionTradingScheme))
            .thenReturn(exists);

        assertEquals(exists, aviationAccountQueryService.isExistingCrcoCode(code, competentAuthority, emissionTradingScheme, null));

        verify(aviationAccountRepository, times(1))
            .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingScheme(code, competentAuthority, emissionTradingScheme);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isExistingAccountNameAndAccountIdNot(boolean exists) {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Long accountId = 1L;

        when(aviationAccountRepository
                .existsByNameAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(accountName, competentAuthority, emissionTradingScheme, accountId))
                .thenReturn(exists);

        assertEquals(exists, aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, accountId));

        verify(aviationAccountRepository, times(1))
                .existsByNameAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(accountName, competentAuthority, emissionTradingScheme, accountId);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isExistingCrcoCodeAndAccountIdNot(boolean exists) {
        String code = "code";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        Long accountId = 1L;

        when(aviationAccountRepository
                .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(code, competentAuthority, emissionTradingScheme, accountId))
                .thenReturn(exists);

        assertEquals(exists, aviationAccountQueryService.isExistingCrcoCode(code, competentAuthority, emissionTradingScheme, accountId));

        verify(aviationAccountRepository, times(1))
                .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(code, competentAuthority, emissionTradingScheme, accountId);
    }




    @Test
    void getAviationAccountsByUserAndSearchCriteria_operator() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        String accountName1 = "account1";
        String accountName2 = "account2";
        AppUser operatorUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.OPERATOR).build();
        operatorUser.setAuthorities(List.of(
                AppAuthority.builder().accountId(accountId1).code("operator").build(),
                AppAuthority.builder().accountId(accountId2).code("operator").build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AviationAccountSearchResultsInfoDTO> accountDTOs =
                List.of(
                        new AviationAccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", AviationAccountStatus.LIVE.name()),
                        new AviationAccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", AviationAccountStatus.LIVE.name()));

        AviationAccountSearchResults results = AviationAccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(aviationAccountRepository.findByAccountIds(List.of(accountId1, accountId2), criteria)).thenReturn(results);

        //invoke
        AviationAccountSearchResults actual = aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(operatorUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(aviationAccountRepository, times(1)).findByAccountIds(List.of(accountId1, accountId2), criteria);
        verifyNoMoreInteractions(aviationAccountRepository);
    }

    @Test
    void getAviationAccountsByUserAndSearchCriteria_regulator() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        String accountName1 = "account1";
        String accountName2 = "account2";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        AppUser regulatorUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.REGULATOR).build();
        regulatorUser.setAuthorities(List.of(
                AppAuthority.builder().competentAuthority(ca).permissions(List.of(Permission.PERM_CA_USERS_EDIT)).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AviationAccountSearchResultsInfoDTO> accountDTOs =
                List.of(
                        new AviationAccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", AviationAccountStatus.LIVE.name()),
                        new AviationAccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", AviationAccountStatus.LIVE.name()));

        AviationAccountSearchResults results = AviationAccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(aviationAccountRepository.findByCompAuth(ca, criteria)).thenReturn(results);

        //invoke
        AviationAccountSearchResults actual = aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(regulatorUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(aviationAccountRepository, times(1)).findByCompAuth(ca, criteria);
        verifyNoMoreInteractions(aviationAccountRepository);
    }

    @Test
    void getAviationAccountsByUserAndSearchCriteria_verifierAdmin() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Set<Long> accountIdsSet = Set.of(accountId1, accountId2);
        List<Long> accountIds = new ArrayList<>(accountIdsSet);
        String accountName1 = "account1";
        String accountName2 = "account2";
        Long vbId = 1L;
        AppUser verifierUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.VERIFIER).build();
        verifierUser.setAuthorities(List.of(
                AppAuthority.builder()
                        .verificationBodyId(vbId).permissions(List.of(Permission.PERM_CA_USERS_EDIT)).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AviationAccountSearchResultsInfoDTO> accountDTOs =
                List.of(
                        new AviationAccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", AviationAccountStatus.LIVE.name()),
                        new AviationAccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", AviationAccountStatus.LIVE.name()));

        AviationAccountSearchResults results = AviationAccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser, AccountType.AVIATION)).thenReturn(accountIdsSet);
        when(aviationAccountRepository.findByAccountIds(accountIds, criteria)).thenReturn(results);

        //invoke
        AviationAccountSearchResults actual = aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(verifierUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(verifierAccountAccessService, times(1)).findAuthorizedAccountIds(verifierUser, AccountType.AVIATION);
        verify(aviationAccountRepository, times(1)).findByAccountIds(accountIds, criteria);
        verifyNoMoreInteractions(aviationAccountRepository);
    }

    @Test
    void getAviationAccountsByUserAndSearchCriteria_verifier() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Set<Long> accountIdsSet = Set.of(accountId1, accountId2);
        List<Long> accountIds = new ArrayList<>(accountIdsSet);
        String accountName1 = "account1";
        String accountName2 = "account2";
        Long vbId = 1L;
        AppUser verifierUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.VERIFIER).build();
        verifierUser.setAuthorities(List.of(
            AppAuthority.builder()
                .verificationBodyId(vbId).permissions(List.of(Permission.PERM_CA_USERS_EDIT)).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AviationAccountSearchResultsInfoDTO> accountDTOs =
            List.of(
                new AviationAccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", AviationAccountStatus.LIVE.name()),
                new AviationAccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", AviationAccountStatus.LIVE.name()));

        AviationAccountSearchResults results = AviationAccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser, AccountType.AVIATION)).thenReturn(accountIdsSet);
        when(aviationAccountRepository.findByAccountIds(accountIds, criteria)).thenReturn(results);

        //invoke
        AviationAccountSearchResults actual = aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(verifierUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AviationAccountSearchResultsInfoDTO::getName)
            .containsOnly(accountName1, accountName2);

        //verify
        verify(verifierAccountAccessService, times(1)).findAuthorizedAccountIds(verifierUser, AccountType.AVIATION);
        verify(aviationAccountRepository, times(1)).findByAccountIds(accountIds, criteria);
        verifyNoMoreInteractions(aviationAccountRepository);
    }

    @ParameterizedTest
    @MethodSource("provideGetAviationAccountDTOByIdAndUserTestArgs")
    void getAviationAccountDTOByIdAndUser(String userRoleType, AviationAccount aviationAccount, AviationAccountDTO expectedResult) {
        Long accountId = aviationAccount.getId();
        AppUser user = AppUser.builder().roleType(userRoleType).build();

        when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(aviationAccount));

        if(userRoleType.equals(RoleTypeConstants.REGULATOR)) {
            when(aviationAccountMapper.toAviationAccountDTO(aviationAccount)).thenReturn(expectedResult);
        }
        else {
            when(aviationAccountMapper.toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount)).thenReturn(expectedResult);
        }

        final AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOByIdAndUser(accountId, user);
        assertThat(aviationAccountDTO).isNotNull();
        assertEquals(expectedResult, aviationAccountDTO);

        verify(aviationAccountRepository, times(1)).findAviationAccountById(accountId);
        if(userRoleType.equals(RoleTypeConstants.REGULATOR)) {
            verify(aviationAccountMapper, times(1)).toAviationAccountDTO(aviationAccount);
        }
        else {
            verify(aviationAccountMapper, times(1)).toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount);
        }
        verifyNoMoreInteractions(aviationAccountMapper);
    }

    @Test
    void getClosedAviationAccountDTOByIdAndUser() {
        final Long accountId = 1L;
        final String crcoCode = "crcoCode";
        final String closureReason = "Test reason";
        final String closedByName = "Regulator name";
        final LocalDateTime closingDate = LocalDateTime.of(2023, 12, 30, 10, 23);
        AviationAccount aviationAccount = AviationAccount.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name("name")
            .status(AviationAccountStatus.CLOSED)
            .reportingStatusHistoryList(List.of(AviationAccountReportingStatusHistory
                    .builder()
                    .status(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
                    .reason("reason")
                    .build()))
            .crcoCode(crcoCode)
            .closureReason(closureReason)
            .closedByName(closedByName)
            .closingDate(closingDate)
            .build();

        AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

        AviationAccountDTO expectedAviationAccountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .name("name")
                .status(AviationAccountStatus.CLOSED)
                .crcoCode(crcoCode)
                .closureReason(closureReason)
                .closedByName(closedByName)
                .closingDate(closingDate)
                .build();

        when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(aviationAccount));
        when(aviationAccountMapper.toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount)).thenReturn(expectedAviationAccountDTO);

        final AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOByIdAndUser(accountId, user);
        assertThat(aviationAccountDTO).isNotNull();
        assertEquals(AviationAccountStatus.CLOSED, aviationAccountDTO.getStatus());
        assertEquals(closureReason, aviationAccountDTO.getClosureReason());
        assertEquals(closedByName, aviationAccountDTO.getClosedByName());
        assertEquals(closingDate, aviationAccountDTO.getClosingDate());

        verify(aviationAccountRepository, times(1)).findAviationAccountById(accountId);
        verify(aviationAccountMapper, times(1)).toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount);
    }
    
    @Test
    void getAviationAccountInfoDTOById() {
        final Long accountId = 1L;
        final String crcoCode = "crcoCode";
        AviationAccount aviationAccount = AviationAccount.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name("name")
            .status(AviationAccountStatus.NEW)
            .crcoCode(crcoCode)
            .build();

        AviationAccountInfoDTO expectedAviationAccountInfo = AviationAccountInfoDTO.builder()
                .id(accountId)
                .name("name")
                .crcoCode(crcoCode)
                .accountType(aviationAccount.getAccountType())
                .competentAuthority(aviationAccount.getCompetentAuthority())
                .emissionTradingScheme(aviationAccount.getEmissionTradingScheme())
                .build();

        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.of(aviationAccount));
        when(aviationAccountMapper.toAviationAccountInfoDTO(aviationAccount)).thenReturn(expectedAviationAccountInfo);

        assertEquals(expectedAviationAccountInfo, aviationAccountQueryService.getAviationAccountInfoDTOById(accountId));

        verify(aviationAccountRepository, times(1)).findById(accountId);
        verify(aviationAccountMapper, times(1)).toAviationAccountInfoDTO(aviationAccount);
    }

    @Test
    void getAviationAccountInfoDTOById_not_found() {
        final Long accountId = 1L;

        when(aviationAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(
            BusinessException.class, () -> aviationAccountQueryService.getAviationAccountInfoDTOById(accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(aviationAccountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountIdsByStatuses() {
        List<AviationAccountStatus> accountStatuses = List.of(AviationAccountStatus.NEW);
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        AviationAccount account1 = AviationAccount.builder().id(accountId1).build();
        AviationAccount account2 = AviationAccount.builder().id(accountId2).build();

        when(aviationAccountRepository.findAllByStatusIn(accountStatuses)).thenReturn(List.of(account1, account2));

        List<Long> actualAccountIds = aviationAccountQueryService.getAccountIdsByStatuses(accountStatuses);

        assertThat(actualAccountIds).containsExactlyInAnyOrder(accountId1, accountId2);

        verify(aviationAccountRepository, times(1)).findAllByStatusIn(accountStatuses);
    }

    @Test
    void getAccountIdsByStatuses_return_empty_list() {
        List<AviationAccountStatus> accountStatuses = List.of(AviationAccountStatus.NEW);
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        AviationAccount account1 = AviationAccount.builder().id(accountId1).build();
        AviationAccount account2 = AviationAccount.builder().id(accountId2).build();

        when(aviationAccountRepository.findAllByStatusIn(accountStatuses)).thenReturn(List.of(account1, account2));

        List<Long> actualAccountIds = aviationAccountQueryService.getAccountIdsByStatuses(accountStatuses);

        assertThat(actualAccountIds).containsExactlyInAnyOrder(accountId1, accountId2);

        verify(aviationAccountRepository, times(1)).findAllByStatusIn(accountStatuses);
    }

    @Test
    void existsAccountById() {
        Long accountId = 1L;

        when(aviationAccountRepository.existsById(accountId)).thenReturn(true);
        assertTrue(aviationAccountQueryService.existsAccountById(accountId));

        when(aviationAccountRepository.existsById(accountId)).thenReturn(false);
        assertFalse(aviationAccountQueryService.existsAccountById(accountId));
    }
    
    @Test
    void getAviationAccountDTOById() {
    	Long accountId = 1L;
		AviationAccount account = AviationAccount.builder()
				.id(accountId)
				.accountType(AccountType.AVIATION)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		
		AviationAccountDTO expected = AviationAccountDTO.builder()
				.id(accountId)
				.accountType(AccountType.AVIATION)
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		
		when(aviationAccountRepository.findAviationAccountById(accountId)).thenReturn(Optional.of(account));
		when(aviationAccountMapper.toAviationAccountDTO(account)).thenReturn(expected);
		
		AviationAccountDTO result = aviationAccountQueryService.getAviationAccountDTOById(accountId);
    	
		assertThat(result).isEqualTo(expected);
		
		verify(aviationAccountRepository, times(1)).findAviationAccountById(accountId);
    }
    
    @Test
    void getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses() {
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	Set<AviationAccountStatus> statuses = Set.of(AviationAccountStatus.LIVE);
    	Set<EmissionTradingScheme> emissionTradingScheme = Set.of(EmissionTradingScheme.UK_ETS_AVIATION);
    	Set<AviationAccountReportingStatus> reportingStatuses = Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
    	
    	Set<AviationAccountIdAndNameDTO> dtos = Set.of(
    			new AviationAccountIdAndNameDTO() {
					@Override
					public String getAccountName() {
						return "accountName";
					}
					
					@Override
					public Long getAccountId() {
						return 1L;
					}
				});
    	
		when(aviationAccountRepository.findAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca, statuses,
				emissionTradingScheme, reportingStatuses)).thenReturn(dtos);
		
		Set<AviationAccountIdAndNameDTO> result = aviationAccountQueryService.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca, statuses,
				emissionTradingScheme, reportingStatuses);
		
		assertThat(result).containsExactly(dtos.iterator().next());
		verify(aviationAccountRepository, times(1)).findAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(
				ca, statuses, emissionTradingScheme, reportingStatuses);
    }

    private static Stream<Arguments> provideGetAviationAccountDTOByIdAndUserTestArgs() {
        Long accountId = 1L;
        String accountName = "name";
        String emitterId = "emitterId";
        String crcoCode = "crcoCode";
        String reportingStatusReason = "reportingStatusReason";
        AviationAccount aviationAccount = AviationAccount.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name(accountName)
            .emitterId(emitterId)
            .status(AviationAccountStatus.NEW)
            .crcoCode(crcoCode)
            .reportingStatusHistoryList(List.of(AviationAccountReportingStatusHistory.builder().status(AviationAccountReportingStatus.EXEMPT_COMMERCIAL).reason(reportingStatusReason).build()))
            .build();

        AviationAccountDTO aviationAccountDTO = AviationAccountDTO.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name(accountName)
            .status(AviationAccountStatus.NEW)
            .crcoCode(crcoCode)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .build();

        AviationAccountDTO aviationAccountDTOForRegulator = AviationAccountDTO.builder()
            .id(accountId)
            .accountType(AccountType.AVIATION)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .name(accountName)
            .status(AviationAccountStatus.NEW)
            .crcoCode(crcoCode)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .reportingStatusReason(reportingStatusReason)
            .build();

        return Stream.of(
            //when operator
            Arguments.of(RoleTypeConstants.OPERATOR, aviationAccount, aviationAccountDTO),
            //when regulator
            Arguments.of(RoleTypeConstants.REGULATOR, aviationAccount, aviationAccountDTOForRegulator),
            //when verifier
            Arguments.of(RoleTypeConstants.VERIFIER, aviationAccount, aviationAccountDTO)
        );
    }

}