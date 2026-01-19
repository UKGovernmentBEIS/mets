package uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.integration.registry.accountcontacts.common.AccountContactRegistryIntegrationUtilityService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountContactNotifyRegistryService {

    private final AccountContactRegistryIntegrationUtilityService utilityService;
    private final InstallationAccountContactRegistryProducer registryProducer;

    @Transactional
    public void notifyRegistry(Account account) {

        if(ObjectUtils.isEmpty(account.getRegistryId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, account.getId(),
                    NotifyRegistryUtils.ACCOUNT_CONTACT_INTEGRATION_POINT_KEY, "Unable to publish account contact " +
                            "event to registry. The Registry/Operator Id field is empty");
            return;
        }

        MetsContactsEvent metsContactsEvent = utilityService.buildMetsContactsEvent(account);

        registryProducer.produce(metsContactsEvent);

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, account.getId(),
                NotifyRegistryUtils.ACCOUNT_CONTACT_INTEGRATION_POINT_KEY, "Account contact event published to registry");

    }



}
