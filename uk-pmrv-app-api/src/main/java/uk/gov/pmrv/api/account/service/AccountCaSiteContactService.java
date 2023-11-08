package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountCaSiteContactService {

    private final AccountRepository accountRepository;
    private final AccountContactQueryService accountContactQueryService;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final ApprovedAccountQueryService approvedAccountQueryService;

    public Optional<String> findCASiteContactByAccount(Long accountId) {
       return accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);
    }

    public AccountContactInfoResponse getAccountsAndCaSiteContacts(PmrvUser pmrvUser, AccountType accountType,
                                                                   Integer page, Integer pageSize) {
        Page<AccountContactInfoDTO> contacts =
            approvedAccountQueryService.getApprovedAccountsAndCaSiteContactsByCa(pmrvUser.getCompetentAuthority(), accountType, page, pageSize);

        // Check if user has the permission of editing account contacts assignees
        boolean isEditable = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, Scope.EDIT_USER);

        // Transform properly
        return AccountContactInfoResponse.builder()
            .contacts(contacts.get().collect(Collectors.toList()))
            .totalItems(contacts.getTotalElements())
            .editable(isEditable)
            .build();
    }
    
    @Transactional
    public void removeUserFromCaSiteContact(String userId) {
        List<Account> accounts = accountRepository.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, userId);
        accounts
            .forEach(ac -> ac.getContacts().remove(AccountContactType.CA_SITE));
    }

    @Transactional
    public void updateCaSiteContacts(PmrvUser pmrvUser, AccountType accountType, List<AccountContactDTO> caSiteContacts) {
        CompetentAuthorityEnum ca = pmrvUser.getCompetentAuthority();

        // Validate accounts belonging to CA
        Set<Long> accountIds =
            caSiteContacts.stream()
                .map(AccountContactDTO::getAccountId)
                .collect(Collectors.toSet());
        validateAccountsByCaAndAccountType(accountIds, ca, accountType);

        // Validate users belonging to CA
        Set<String> userIds = caSiteContacts.stream()
            .map(AccountContactDTO::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        validateUsersByCA(userIds, ca);

        // Update contacts in DB
        doUpdateCaSiteContacts(caSiteContacts);
    }

    private void doUpdateCaSiteContacts(List<AccountContactDTO> caSiteContactsUpdate) {
        List<Long> accountIdsUpdate =
                caSiteContactsUpdate.stream()
                        .map(AccountContactDTO::getAccountId)
                        .collect(Collectors.toList());
        List<Account> accounts = accountRepository.findAllByIdIn(accountIdsUpdate);

        caSiteContactsUpdate
            .forEach(contact -> accounts.stream()
                .filter(ac -> ac.getId().equals(contact.getAccountId()))
                .findFirst()
                .ifPresent(ac -> {
                    ac.getContacts().put(AccountContactType.CA_SITE, contact.getUserId());
                }));
    }

    /** Validates that account exists and belongs to CA */
    private void validateAccountsByCaAndAccountType(Set<Long> accountIds, CompetentAuthorityEnum ca, AccountType accountType) {
        List<Long> accounts = approvedAccountQueryService.getAllApprovedAccountIdsByCa(ca, accountType);

        if(!accounts.containsAll(accountIds)){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_RELATED_TO_CA);
        }
    }

    /** Validates that user exists and belongs to CA */
    private void validateUsersByCA(Set<String> userIds, CompetentAuthorityEnum ca) {
        List<String> users = regulatorAuthorityResourceService.findUsersByCompetentAuthority(ca);

        if(!users.containsAll(userIds)){
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
    }
}
