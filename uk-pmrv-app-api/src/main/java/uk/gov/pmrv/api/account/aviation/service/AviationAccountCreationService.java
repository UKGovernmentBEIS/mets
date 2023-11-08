package uk.gov.pmrv.api.account.aviation.service;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountCreatedEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.service.AccountIdentifierService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@Validated
@Service
@RequiredArgsConstructor
public class AviationAccountCreationService {

    private final AviationAccountRepository aviationAccountRepository;
    private final AccountIdentifierService accountIdentifierService;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final ApplicationEventPublisher publisher;
    private final AviationAccountMapper aviationAccountMapper;

    @Transactional
    public void createAccount(@Valid AviationAccountCreationDTO aviationAccountCreationDTO, PmrvUser pmrvUser) {
        EmissionTradingScheme emissionTradingScheme = aviationAccountCreationDTO.getEmissionTradingScheme();
        CompetentAuthorityEnum competentAuthority = pmrvUser.getCompetentAuthority();

        validateAccountNameUniqueness(aviationAccountCreationDTO.getName(), competentAuthority, emissionTradingScheme);
        validateCrcoCodeUniqueness(aviationAccountCreationDTO.getCrcoCode(), competentAuthority, emissionTradingScheme);

        final Long identifier = accountIdentifierService.incrementAndGet();

        // Create and persist account
        AviationAccount account = aviationAccountMapper.toAviationAccount(aviationAccountCreationDTO, competentAuthority, identifier);
        account.setAcceptedDate(LocalDateTime.now());
        account.setCreatedDate(LocalDateTime.now());
        account.setCreatedByUserId(pmrvUser.getUserId());
        
        final AviationAccountReportingStatus initialReportingStatus = AviationAccountReportingStatus.REQUIRED_TO_REPORT;
        account.setReportingStatus(initialReportingStatus);
        account.addReportingStatusHistory(AviationAccountReportingStatusHistory.builder()
                .status(initialReportingStatus)
                .submitterId(pmrvUser.getUserId())
                .submitterName(pmrvUser.getFullName())
                .build());
        
        aviationAccountRepository.save(account);

        publisher.publishEvent(AviationAccountCreatedEvent.builder()
            .accountId(account.getId())
            .emissionTradingScheme(account.getEmissionTradingScheme())
            .build());
    }

    private void validateAccountNameUniqueness(String name, CompetentAuthorityEnum competentAuthority,
                                              EmissionTradingScheme emissionTradingScheme) {
        if(aviationAccountQueryService.isExistingAccountName(name, competentAuthority, emissionTradingScheme)) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_EXISTS, name, competentAuthority, emissionTradingScheme);
        }
    }

    private void validateCrcoCodeUniqueness(String crcoCode, CompetentAuthorityEnum competentAuthority,
                                           EmissionTradingScheme emissionTradingScheme) {
        if(aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme)) {
            throw new BusinessException(ErrorCode.CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT,
                crcoCode, competentAuthority, emissionTradingScheme);
        }
    }

}
