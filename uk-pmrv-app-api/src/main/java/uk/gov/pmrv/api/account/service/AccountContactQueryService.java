package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountContactQueryService {

    private final AccountRepository accountRepository;

    private final AuthorityService authorityService;

    private final OperatorUserAuthService operatorUserAuthService;

    public Optional<String> findPrimaryContactByAccount(Long accountId) {
        return findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
    }

    public Optional<String> findContactByAccountAndContactType(Long accountId, AccountContactType accountContactType) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt
                .map(Account::getContacts)
                .map(contacts -> contacts.get(accountContactType));
    }

    public List<AccountContactInfoDTO> findContactsByAccountIdsAndContactType(Set<Long> accountIds, AccountContactType accountContactType) {
        return accountRepository
                .findAccountContactsByAccountIdsAndContactType(new ArrayList<>(accountIds), accountContactType);
    }

    @Transactional(readOnly = true)
    public Map<AccountContactType, String> findContactTypesByAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent() && !accountOpt.get().getContacts().isEmpty()) {
            return new EnumMap<>(accountOpt.get().getContacts());
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public Map<AccountContactType, String> findOperatorContactTypesByAccount(Long accountId) {
        Map<AccountContactType, String> contactTypesByAccount = findContactTypesByAccount(accountId);
        return contactTypesByAccount.entrySet().stream()
                .filter(accountContactType -> AccountContactType.getOperatorAccountContactTypes().contains(accountContactType.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Transactional(readOnly = true)
    public Optional<ServiceContactDetails> getServiceContactDetails(Long accountId) {
        return findContactByAccountAndContactType(accountId, AccountContactType.SERVICE)
                .map(userId -> {
                    final String roleCode = authorityService.findAuthorityByUserIdAndAccountId(userId, accountId)
                            .map(AuthorityInfoDTO::getCode)
                            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
                    final OperatorUserDTO operatorUser = operatorUserAuthService.getOperatorUserById(userId);
                    return ServiceContactDetails.builder()
                            .name(operatorUser.getFullName())
                            .email(operatorUser.getEmail())
                            .roleCode(roleCode)
                            .build();
                });
    }
}
