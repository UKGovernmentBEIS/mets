package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountQueryService implements AccountAuthorityInfoProvider {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public CompetentAuthorityEnum getAccountCa(Long accountId) {
        return getAccountById(accountId).getCompetentAuthority();
    }
    
    public Location getAccountContactAddress(Long accountId) {
        return getAccountById(accountId).getLocation();
    }

    public String getAccountName(Long accountId) {
        return getAccountById(accountId).getName();
    }

    public AccountStatus getAccountStatus(Long accountId) {
        return getAccountById(accountId).getStatus();
    }

    public AccountType getAccountType(Long accountId) {
        return getAccountById(accountId).getAccountType();
    }

    public EmissionTradingScheme getAccountEmissionTradingScheme(Long accountId) {
        return getAccountById(accountId).getEmissionTradingScheme();
    }

    public Account exclusiveLockAccount(final Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    @Override
    public Optional<Long> getAccountVerificationBodyId(Long accountId) {
        return Optional.ofNullable(getAccountById(accountId).getVerificationBodyId());
    }

    public AccountInfoDTO getAccountInfoDTOById(Long accountId) {
        return accountMapper.toAccountInfoDTO(getAccountById(accountId));
    }

    public List<AccountInfoDTO> getAccountsInfoByIds(List<Long> accountIds) {
        return accountRepository.findAccountsWithLeByIdIn(accountIds)
            .stream()
            .map(accountMapper::toAccountInfoDTO)
            .collect(Collectors.toList());
    }

    public Set<Long> getAccountIdsByAccountType(List<Long> accountIds, AccountType accountType) {
        return accountRepository.findAllByIdInAndAccountType(accountIds, accountType)
            .stream()
            .map(Account::getId)
            .collect(Collectors.toSet());
    }

    public boolean isExistingActiveAccountName(String accountName) {
        return false;
    }

    public boolean isExistingActiveAccountNameAndIdNot(String accountName, Long accountId) {
        return false;
    }

    /**
     * Checks if there is an account with the given name but different account Id than the given one
     * @param accountName   the account name to look for
     * @param accountId     the account Id
     * @return              true if there is an account/false if there is not such an account
     */
    public boolean isExistingActiveAccountName(String accountName, Long accountId) {
        return accountId != null ? isExistingActiveAccountNameAndIdNot(accountName, accountId) :
                isExistingActiveAccountName(accountName);
    }

    public boolean isLegalEntityUnused(Long legalEntityId) {
        return accountRepository.countByLegalEntityId(legalEntityId) == 0;
    }

    Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public Account findAccountByRegistryId(Integer registryId) {
        return accountRepository.findAccountByRegistryId(registryId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
