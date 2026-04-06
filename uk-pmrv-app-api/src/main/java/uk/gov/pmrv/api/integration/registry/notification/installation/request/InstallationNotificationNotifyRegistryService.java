package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.common.RegistryIdEmailNotifierService;
import uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction.NotificationRegistryIntegrationAddRequestActionService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationNotificationNotifyRegistryService {

    private final InstallationNotificationRegistryProducer registryProducer;
    private final InstallationAccountQueryService accountQueryService;
    private final FileDocumentService fileDocumentService;
    private final NotificationRegistryIntegrationAddRequestActionService addRequestActionService;
    private final RegistryIdEmailNotifierService notifierService;



    @Transactional
    public void notifyRegistry(NotificationRegistryEvent notificationRegistryEvent) {

        InstallationAccountDTO accountDTO = accountQueryService.getAccountDTOById(notificationRegistryEvent.getAccountId());

        if(!validateAccount(accountDTO)) {
            return;
        }

        FileDTO fileDTO = fileDocumentService.getFileDTO(notificationRegistryEvent.getFileInfoDTO().getUuid());

        RegulatorNoticeEvent regulatorNoticeEvent =
                RegulatorNoticeEvent.builder()
                        .registryId(String.valueOf(accountDTO.getRegistryId()))
                        .fileName(notificationRegistryEvent.getFileInfoDTO().getName())
                        .fileData(fileDTO.getFileContent())
                        .type(notificationRegistryEvent.getRegistryNotificationType().getName())
                        .build();

        registryProducer.produce(regulatorNoticeEvent);

        addRequestActionService.addRequestAction(notificationRegistryEvent.getRequestId(),
                regulatorNoticeEvent,notificationRegistryEvent.getFileInfoDTO());

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, notificationRegistryEvent.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_INSTALLATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                "Notification event published to registry");

    }

    private boolean validateAccount(InstallationAccountDTO accountDTO) {

        if(accountDTO.getRegistryId()==null) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, accountDTO.getId(),
                    NotifyRegistryUtils.ACCOUNT_INSTALLATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                    "Unable to publish notification event to registry. The Registry/Operator Id field is empty");
            notifierService.registryIdNonExistenceNotifyRegulator(accountDTO,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_NOTIFICATION_MISSING_REGISTRY_ID.getName(),
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY);
            return false;
        }

        return EmitterType.GHGE.equals(accountDTO.getEmitterType())
                && EmissionTradingScheme.UK_ETS_INSTALLATIONS.equals(accountDTO.getEmissionTradingScheme());
    }

}
