package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.common.RegistryIdEmailNotifierService;
import uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.requestaction.WithholdFlagRegistryIntegrationAddRequestActionService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.time.Year;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagNotifyRegistryService {

    private final InstallationWithholdFlagRegistryProducer registryProducer;
    private final InstallationAccountQueryService accountQueryService;
    private final WithholdFlagRegistryIntegrationAddRequestActionService addRequestActionService;
    private final RegistryIdEmailNotifierService notifierService;

    @Transactional
    public void notifyRegistry(WithholdFlagRegistryEvent withholdFlagRegistryEvent) {

       InstallationAccountDTO accountDTO = accountQueryService.getAccountDTOById(withholdFlagRegistryEvent.getAccountId());

       if(!validateAccount(accountDTO)) {
            return;
       }

       Integer registryId = accountDTO.getRegistryId();

        AccountWithholdUpdateEvent accountWithholdUpdateEvent =
                AccountWithholdUpdateEvent.builder()
                        .registryId(Long.valueOf(registryId))
                        .reportingYear(Year.of(withholdFlagRegistryEvent.getYear()))
                        .withholdFlag(withholdFlagRegistryEvent.getWithholdFlag())
                        .build();

        registryProducer.produce(accountWithholdUpdateEvent);

        if(!withholdFlagRegistryEvent.isFromSetOperatorId()) {
            addRequestActionService.addRequestAction(withholdFlagRegistryEvent.getRequestId(), accountWithholdUpdateEvent);
        }

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, withholdFlagRegistryEvent.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_INSTALLATION_WITHHOLD_FLAG_INTEGRATION_POINT_KEY,
                "Withhold flag update event published to registry");

    }

    private boolean validateAccount(InstallationAccountDTO accountDTO) {

        if(accountDTO.getRegistryId()==null) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, accountDTO.getId(),
                    NotifyRegistryUtils.ACCOUNT_INSTALLATION_WITHHOLD_FLAG_INTEGRATION_POINT_KEY,
                    "Unable to publish withhold flag event to registry. The Registry/Operator Id field is empty");
            notifierService.registryIdNonExistenceNotifyRegulator(accountDTO,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_WITHHOLD_FLAG_MISSING_REGISTRY_ID.getName(),
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY);
            return false;
        }

        return EmitterType.GHGE.equals(accountDTO.getEmitterType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountDTO.getEmissionTradingScheme());
    }

}
