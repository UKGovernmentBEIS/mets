package uk.gov.pmrv.api.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountCreatedEvent;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.service.AccountIdentifierService;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountCreationServiceTest {

    @InjectMocks
    private AviationAccountCreationService aviationAccountCreationService;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private AccountIdentifierService accountIdentifierService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AviationAccountMapper aviationAccountMapper;

    @Test
    void createAccount() {
        Long accountId = 9098L;
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        Long sopId = 9807L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String emitterId = "EM09098";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
            .build();

        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .sopId(sopId)
            .commencementDate(commencementDate)
            .build();

        AviationAccount aviationAccount = AviationAccount.builder()
                .id(accountId)
                .emissionTradingScheme(emissionTradingScheme)
                .status(AviationAccountStatus.NEW)
                .emitterId(emitterId)
                .name(accountName)
                .build();

        when(aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme)).thenReturn(false);
        when(accountIdentifierService.incrementAndGet()).thenReturn(accountId);
        when(aviationAccountMapper.toAviationAccount(accountCreationDTO, competentAuthority, accountId)).thenReturn(aviationAccount);

        aviationAccountCreationService.createAccount(accountCreationDTO, appUser);

        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(accountName, competentAuthority, emissionTradingScheme);
        verify(aviationAccountQueryService, times(1))
            .isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme);
        verify(accountIdentifierService, times(1)).incrementAndGet();
        verify(aviationAccountMapper, times(1)).toAviationAccount(accountCreationDTO, competentAuthority, accountId);
        verify(eventPublisher, times(1))
            .publishEvent(AviationAccountCreatedEvent.builder()
                .accountId(accountId)
                .emissionTradingScheme(emissionTradingScheme)
                .build());

        ArgumentCaptor<AviationAccount> accountCaptor = ArgumentCaptor.forClass(AviationAccount.class);
        verify(aviationAccountRepository, times(1)).save(accountCaptor.capture());
        AviationAccount accountCaptured = accountCaptor.getValue();

        assertEquals(AviationAccountStatus.NEW, accountCaptured.getStatus());
        assertEquals(emitterId, accountCaptured.getEmitterId());
        assertEquals(accountName, accountCaptured.getName());
        assertThat(accountCaptured.getReportingStatus()).isEqualTo(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
    }

    @Test
    void createAccount_name_already_exists() {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        Long sopId = 9807L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
            .build();

        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .sopId(sopId)
            .commencementDate(commencementDate)
            .build();

        when(aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class,
            () ->aviationAccountCreationService.createAccount(accountCreationDTO, appUser));

        assertEquals(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS, be.getErrorCode());

        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(accountName, competentAuthority, emissionTradingScheme);
        verify(aviationAccountQueryService, never()).isExistingCrcoCode(anyString(), any(), any());
        verifyNoInteractions(accountIdentifierService, aviationAccountRepository, aviationAccountMapper, eventPublisher);
    }

    @Test
    void createAccount_crco_code_already_exists() {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        Long sopId = 9807L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
            .build();

        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .sopId(sopId)
            .commencementDate(commencementDate)
            .build();

        when(aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme)).thenReturn(false);
        when(aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class,
            () ->aviationAccountCreationService.createAccount(accountCreationDTO, appUser));

        assertEquals(MetsErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT, be.getErrorCode());

        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(accountName, competentAuthority, emissionTradingScheme);
        verify(aviationAccountQueryService, times(1))
            .isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme);
        verifyNoInteractions(accountIdentifierService, aviationAccountRepository, aviationAccountMapper, eventPublisher);
    }
}