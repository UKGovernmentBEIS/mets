package uk.gov.pmrv.api.account.installation.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.account.service.VerifierAccountAccessByAccountTypeService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Service
@RequiredArgsConstructor
public class InstallationAccountQueryService {

    private final InstallationAccountRepository installationAccountRepository;
    private final AccountQueryService accountQueryService;
    private final InstallationAccountMapper accountInstallationMapper;
    private final VerifierAccountAccessByAccountTypeService verifierAccountAccessService;

    public InstallationAccountDTO getAccountDTOById(Long accountId) {
        return accountInstallationMapper.toInstallationAccountDTO(getAccountFullInfoById(accountId));
    }

    public InstallationAccountWithoutLeHoldingCompanyDTO getAccountWithoutLeHoldingCompanyDTOById(Long accountId) {
        return accountInstallationMapper.toInstallationAccountWithoutLeHoldingCompany(getAccountWithLocAndLeWithLocById(accountId));
    }

    public InstallationAccountInfoDTO getInstallationAccountInfoDTOById(Long accountId) {
        return installationAccountRepository.findById(accountId)
            .map(accountInstallationMapper::toInstallationAccountInfoDTO)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public List<Long> getAccountIdsByStatus(InstallationAccountStatus status) {
        return installationAccountRepository.findAllByStatusIs(status).stream()
            .map(InstallationAccount::getId)
            .toList();
    }
    
	public Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
			CompetentAuthorityEnum ca, Set<InstallationAccountStatus> statuses,
			Set<EmitterType> emitterTypes, Set<InstallationCategory> categories) {
		return installationAccountRepository.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
				ca,
				statuses == null ? Set.of() : statuses, 
				emitterTypes == null ? Set.of() : emitterTypes,
				categories == null ? Set.of() : categories);
	}

    public boolean existsAccountById(Long accountId) {
        return installationAccountRepository.existsById(accountId);
    }
    
    public AccountSearchResults getAccountsByUserAndSearchCriteria(PmrvUser user,
                                                                   AccountSearchCriteria searchCriteria) {
        switch (user.getRoleType()) {
            case OPERATOR -> {
                return installationAccountRepository.findByAccountIds(List.copyOf(user.getAccounts()), searchCriteria);
            }
            case REGULATOR -> {
                return installationAccountRepository.findByCompAuth(user.getCompetentAuthority(), searchCriteria);
            }
            case VERIFIER -> {
                    final Set<Long> accounts = verifierAccountAccessService.findAuthorizedAccountIds(user, AccountType.INSTALLATION);
                    return accounts.isEmpty() ?
                        AccountSearchResults.builder().total(0L).accounts(List.of()).build() :
                        installationAccountRepository.findByAccountIds(new ArrayList<>(accounts), searchCriteria);
            }
            default -> throw new UnsupportedOperationException(
                String.format("Fetching installation accounts for role type %s is not supported", user.getRoleType())
            );
        }
    }

    public boolean isLegalEntityUnused(Long legalEntityId) {
        return accountQueryService.isLegalEntityUnused(legalEntityId);
    }

    public boolean transferCodeExists(final String transferCode) {
        return installationAccountRepository.existsByTransferCode(transferCode);
    }

    public Optional<InstallationAccountDTO> getByActiveTransferCode(final String transferCode) {
        return installationAccountRepository.findAccountByTransferCodeAndTransferCodeStatus(transferCode, TransferCodeStatus.ACTIVE)
            .map(accountInstallationMapper::toInstallationAccountDTO);
    }

    void validateAccountNameExistence(String accountName) {
        if (accountQueryService.isExistingActiveAccountName(accountName)) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
    }

    InstallationAccount getAccountFullInfoById(Long accountId) {
        return installationAccountRepository.findAccountFullInfoById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    InstallationAccount getAccountById(Long accountId) {
        return installationAccountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    InstallationCategory getAccountInstallationCategoryById(Long accountId) {
        return getAccountById(accountId).getInstallationCategory();
    }

    InstallationAccount getAccountWithLeById(Long accountId) {
        return installationAccountRepository.findAccountWithLeById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    InstallationAccount getAccountWithLocAndLeWithLocById(Long accountId) {
        return installationAccountRepository.findAccountWithLocAndLeWithLocById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
