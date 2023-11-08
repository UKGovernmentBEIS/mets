package uk.gov.pmrv.api.account.aviation.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.service.VerifierAccountAccessByAccountTypeService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Service
@RequiredArgsConstructor
public class AviationAccountQueryService {

    private final AviationAccountRepository aviationAccountRepository;
    private final AviationAccountMapper aviationAccountMapper;
    private final VerifierAccountAccessByAccountTypeService verifierAccountAccessService;

    public boolean isExistingAccountName(String name, CompetentAuthorityEnum competentAuthority,
                                            EmissionTradingScheme emissionTradingScheme, Long accountId) {
        return accountId != null ? aviationAccountRepository
                .existsByNameAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(name, competentAuthority, emissionTradingScheme, accountId) :
                isExistingAccountName(name, competentAuthority, emissionTradingScheme);
    }

    public boolean isExistingCrcoCode(String crcoCode, CompetentAuthorityEnum competentAuthority,
                                            EmissionTradingScheme emissionTradingScheme, Long accountId) {
        return accountId != null ? aviationAccountRepository
                .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingSchemeAndIdNot(crcoCode, competentAuthority, emissionTradingScheme, accountId) :
                isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme);
    }

    public boolean isExistingAccountName(String name, CompetentAuthorityEnum competentAuthority,
                                         EmissionTradingScheme emissionTradingScheme) {
        return aviationAccountRepository
            .existsByNameAndCompetentAuthorityAndEmissionTradingScheme(name, competentAuthority, emissionTradingScheme);
    }

    public boolean isExistingCrcoCode(String crcoCode, CompetentAuthorityEnum competentAuthority,
                                      EmissionTradingScheme emissionTradingScheme) {
        return aviationAccountRepository
            .existsByCrcoCodeAndCompetentAuthorityAndEmissionTradingScheme(crcoCode, competentAuthority, emissionTradingScheme);
    }

    AviationAccount getAccountById(Long accountId) {
        return aviationAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public AviationAccountSearchResults getAviationAccountsByUserAndSearchCriteria(PmrvUser user,
                                                                                   AccountSearchCriteria searchCriteria) {
        switch (user.getRoleType()) {
            case OPERATOR -> {
                return aviationAccountRepository.findByAccountIds(List.copyOf(user.getAccounts()), searchCriteria);
            }
            case REGULATOR -> {
                return aviationAccountRepository.findByCompAuth(user.getCompetentAuthority(), searchCriteria);
            }
            case VERIFIER -> {
                final Set<Long> accounts = verifierAccountAccessService.findAuthorizedAccountIds(user, AccountType.AVIATION);
                return accounts.isEmpty() ?
                    AviationAccountSearchResults.builder().total(0L).accounts(List.of()).build() :
                    aviationAccountRepository.findByAccountIds(new ArrayList<>(accounts), searchCriteria);
            }
            default -> throw new UnsupportedOperationException(
                String.format("Fetching aviation accounts for role type %s is not supported", user.getRoleType())
            );
        }
    }

    public AviationAccountInfoDTO getAviationAccountInfoDTOById(Long accountId) {
        return aviationAccountRepository.findById(accountId)
            .map(aviationAccountMapper::toAviationAccountInfoDTO)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public AviationAccountDTO getAviationAccountDTOById(Long accountId) {
    	return aviationAccountMapper.toAviationAccountDTO(getAviationAccountById(accountId));
    }

    @Transactional
    public AviationAccountDTO getAviationAccountDTOByIdAndUser(Long accountId, PmrvUser user) {
        AviationAccount aviationAccount = getAviationAccountById(accountId);

        return switch (user.getRoleType()) {
            case REGULATOR ->
                    aviationAccountMapper.toAviationAccountDTO(aviationAccount);
            case OPERATOR, VERIFIER ->
                    aviationAccountMapper.toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount);
        };
    }

    public List<Long> getAccountIdsByStatuses(List<AviationAccountStatus> accountStatuses) {
        return aviationAccountRepository.findAllByStatusIn(accountStatuses).stream()
            .map(AviationAccount::getId)
            .toList();
    }

    public boolean existsAccountById(Long accountId) {
        return aviationAccountRepository.existsById(accountId);
    }
    
	public Set<AviationAccountIdAndNameDTO> getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(
			CompetentAuthorityEnum ca, Set<AviationAccountStatus> statuses, Set<EmissionTradingScheme> emissionTradingSchemes,
			Set<AviationAccountReportingStatus> reportingStatuses) {
		return aviationAccountRepository.findAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
				statuses == null ? Set.of() : statuses,
				emissionTradingSchemes == null ? Set.of() : emissionTradingSchemes,
				reportingStatuses == null ? Set.of() : reportingStatuses);
	}

    private AviationAccount getAviationAccountById(Long accountId) {
        return aviationAccountRepository.findAviationAccountById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
