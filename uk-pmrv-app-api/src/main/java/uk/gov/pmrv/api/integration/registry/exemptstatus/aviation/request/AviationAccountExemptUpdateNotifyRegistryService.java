package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptUpdateNotifyRegistryService {

    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAccountExemptUpdateRegistryProducer producer;

    @Transactional
    public void notifyRegistry(AviationAccountExemptFlagEvent event) {

        if(!aviationAccountQueryService.registryIdExistsForAccount(event.getAccountId())) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                    NotifyRegistryUtils.ACCOUNT_AVIATION_EXEMPT_UPDATE_INTEGRATION_POINT_KEY, "Unable to publish aviation " +
                            "account exempt event to registry. The Registry/Operator Id field is empty");
            return;
        }

        AccountExemptionUpdateEvent accountExemptionUpdateEvent = buildExemptionEvent(event);
        producer.produce(accountExemptionUpdateEvent);
        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_AVIATION_EXEMPT_UPDATE_INTEGRATION_POINT_KEY, "Published aviation account" +
                        " exempt event to registry");


    }

    private AccountExemptionUpdateEvent buildExemptionEvent(AviationAccountExemptFlagEvent event) {
        AccountExemptionUpdateEvent accountExemptionUpdateEvent = new AccountExemptionUpdateEvent();
        accountExemptionUpdateEvent.setExemptionFlag(event.isExempt());
        accountExemptionUpdateEvent.setRegistryId(event.getRegistryId().longValue());
        accountExemptionUpdateEvent.setReportingYear(event.getYear());
        return accountExemptionUpdateEvent;
    }

}
