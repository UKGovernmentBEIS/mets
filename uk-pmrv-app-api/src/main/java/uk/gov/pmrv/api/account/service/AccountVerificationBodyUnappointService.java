package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.event.AccountsVerificationBodyUnappointedEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyUnappointService {
    
    private final AccountRepository accountRepository;
    private final AccountVbSiteContactService accountVbSiteContactService;
    private final AccountVerificationBodyNotificationService accountVerificationBodyNotificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(
            Long verificationBodyId, Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes) {
        if(notAvailableAccreditationEmissionTradingSchemes.isEmpty()) {
            return;
        }
        
        Set<Account> accountsToBeUnappointed = accountRepository.findAllByVerificationBodyAndEmissionTradingSchemeWithContactsIn(
                verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);

        unappointAccounts(accountsToBeUnappointed);
    }

    @Transactional
    public void unappointAccountsAppointedToVerificationBody(Set<Long> verificationBodyIds) {
        Set<Account> accountsToBeUnappointed = accountRepository.findAllByVerificationWithContactsBodyIn(verificationBodyIds);
        unappointAccounts(accountsToBeUnappointed);
    }

    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    @Transactional
    public void unappointAccountAppointedToVerificationBody(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        unappointAccounts(Set.of(account));
    }

    private void unappointAccounts(Set<Account> accountsToBeUnappointed) {
        if(accountsToBeUnappointed.isEmpty()) {
            return;
        }
        
        //clear verification body of accounts
        accountsToBeUnappointed.forEach(account -> account.setVerificationBodyId(null));
        
        accountVbSiteContactService.removeVbSiteContactFromAccounts(accountsToBeUnappointed);

        // Notify users for unappointment
        accountVerificationBodyNotificationService.notifyUsersForVerificationBodyUnapppointment(accountsToBeUnappointed);

        eventPublisher.publishEvent(AccountsVerificationBodyUnappointedEvent.builder()
            .accountIds(accountsToBeUnappointed.stream()
                .map(Account::getId)
                .collect(Collectors.toSet())
            )
            .build()
        );
    }

}
