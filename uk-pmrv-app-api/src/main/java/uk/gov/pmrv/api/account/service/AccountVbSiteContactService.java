package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.VerificationBodyAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountVbSiteContactService {

    private final AccountRepository accountRepository;
    private final VerifierAuthorityResourceService verifierAuthorityResourceService;
    private final VerificationBodyAuthorizationResourceService verificationBodyAuthorizationResourceService;
    private final AccountContactQueryService accountContactQueryService;

    public AccountContactVbInfoResponse getAccountsAndVbSiteContacts(AppUser appUser, AccountType accountType, Integer page, Integer pageSize) {
        Long vbId = appUser.getVerificationBodyId();

        Page<AccountContactVbInfoDTO> contacts = accountRepository
            .findAccountContactsByAccountTypeAndVbAndContactType(PageRequest.of(page, pageSize), accountType, vbId, AccountContactType.VB_SITE);

        // Check if user has the permission of editing account contacts assignees
        boolean isEditable = verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(appUser, vbId, Scope.EDIT_USER);

        // Transform properly
        return AccountContactVbInfoResponse.builder()
                .contacts(contacts.get().collect(Collectors.toList()))
                .totalItems(contacts.getTotalElements())
                .editable(isEditable)
                .build();
    }

    @Transactional
    public void updateVbSiteContacts(AppUser appUser, AccountType accountType, List<AccountContactDTO> vbSiteContacts) {
        Long vbId = appUser.getVerificationBodyId();

        // Validate accounts belonging to VB
        Set<Long> accountIds = vbSiteContacts.stream()
                .map(AccountContactDTO::getAccountId)
                .collect(Collectors.toSet());
        validateAccountsByAccountTypeAndVb(accountIds, vbId, accountType);

        // Validate users belonging to VB
        Set<String> userIds = vbSiteContacts.stream()
                .map(AccountContactDTO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        validateUsersByVb(userIds, vbId);

        // Update contacts in DB
        doUpdateVbSiteContacts(vbSiteContacts);
    }

    public Optional<String> getVBSiteContactByAccount(Long accountId) {
        return accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.VB_SITE);
    }

    @Transactional
    public void removeUserFromVbSiteContact(String userId) {
        List<Account> accounts = accountRepository.findAccountsByContactTypeAndUserId(AccountContactType.VB_SITE, userId);
        accounts.forEach(ac -> ac.getContacts().remove(AccountContactType.VB_SITE));
    }

    @Transactional
    public void removeVbSiteContactFromAccounts(Set<Account> accounts) {
        accounts.forEach(account -> account.getContacts().remove(AccountContactType.VB_SITE));
    }

    private void doUpdateVbSiteContacts(List<AccountContactDTO> vbSiteContacts) {
        List<Long> accountIdsUpdate = vbSiteContacts.stream()
                        .map(AccountContactDTO::getAccountId)
                        .collect(Collectors.toList());
        List<Account> accounts = accountRepository.findAllByIdIn(accountIdsUpdate);

        vbSiteContacts.forEach(contact -> accounts.stream()
                .filter(ac -> ac.getId().equals(contact.getAccountId()))
                .findFirst().ifPresent(ac -> {
                    ac.getContacts().put(AccountContactType.VB_SITE, contact.getUserId());
                }));
    }

    /** Validates that account exists and belongs to VB */
    private void validateAccountsByAccountTypeAndVb(Set<Long> accountIds, Long vbId, AccountType accountType) {
        List<Long> accounts = accountRepository.findAllIdsByAccountTypeAndVB(accountType, vbId);

        if(!new HashSet<>(accounts).containsAll(accountIds)){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_RELATED_TO_VB);
        }
    }

    /** Validates that user exists and belongs to VB */
    private void validateUsersByVb(Set<String> userIds, Long vbId) {
        List<String> users = verifierAuthorityResourceService.findUsersByVerificationBodyId(vbId);

        if(!new HashSet<>(users).containsAll(userIds)){
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
        }
    }
}
