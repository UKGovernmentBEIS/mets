package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.event.AccountContactRegistryEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcontacts.aviation.request.AviationAccountContactNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request.InstallationAccountContactNotifyRegistryService;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class AccountContactEventListener {

    private final InstallationAccountContactNotifyRegistryService installationAccountContactNotifyRegistryService;
    private final AviationAccountContactNotifyRegistryService aviationAccountContactNotifyRegistryService;
    private final AccountRepository accountRepository;

    @EventListener(AccountContactRegistryEvent.class)
    @Transactional
    public void handleAccountContactRegistryEvent(AccountContactRegistryEvent event) {

        event.getAccountsIds().forEach(accountId -> {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

            if(AccountType.INSTALLATION.equals(account.getAccountType())) {
                if(EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(account.getEmissionTradingScheme())) {
                    installationAccountContactNotifyRegistryService.notifyRegistry(account);
                }
            }
            else if(AccountType.AVIATION.equals(account.getAccountType())) {
                if(EmissionTradingScheme.UK_ETS_AVIATION.equals(account.getEmissionTradingScheme())) {
                    aviationAccountContactNotifyRegistryService.notifyRegistry(account);
                }
            }
        });

    }


}
