package uk.gov.pmrv.api.integration.registry.notification.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.notification.aviation.request.requestaction.AviationNotificationRegistryIntegrationAddRequestActionService;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class AviationNotificationNotifyRegistryService {

    private final AviationNotificationRegistryProducer registryProducer;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final FileDocumentService fileDocumentService;
    private final AviationNotificationRegistryIntegrationAddRequestActionService addRequestActionService;


    @Transactional
    public void notifyRegistry(AviationNotificationRegistryEvent notificationRegistryEvent) {

        AviationAccountDTO accountDTO = aviationAccountQueryService.getAviationAccountDTOById(notificationRegistryEvent.getAccountId());

        if(!validateAccount(accountDTO)) {
            return;
        }

        FileDTO fileDTO = null;
        if(notificationRegistryEvent.getFileInfoDTO()!=null) {
            fileDTO = fileDocumentService.getFileDTO(notificationRegistryEvent.getFileInfoDTO().getUuid());
        }

        RegulatorNoticeEvent regulatorNoticeEvent =
                RegulatorNoticeEvent.builder()
                        .registryId(String.valueOf(accountDTO.getRegistryId()))
                        .fileName(fileDTO!=null ? fileDTO.getFileName() : null)
                        .fileData(fileDTO!=null ? fileDTO.getFileContent() : null)
                        .type(notificationRegistryEvent.getRegistryNotificationType().getName())
                        .build();

        registryProducer.produce(regulatorNoticeEvent);

        addRequestActionService.addRequestAction(
                notificationRegistryEvent,
                accountDTO.getRegistryId()
        );

        log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, notificationRegistryEvent.getAccountId(),
                NotifyRegistryUtils.ACCOUNT_AVIATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                "Notification event published to registry");

    }

    private boolean validateAccount(AviationAccountDTO accountDTO) {

        if(accountDTO.getRegistryId()==null) {
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, accountDTO.getId(),
                    NotifyRegistryUtils.ACCOUNT_AVIATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                    "Unable to publish notification event to registry. The Registry/Operator Id field is empty");
            return false;
        }

        return EmissionTradingScheme.UK_ETS_AVIATION.equals(accountDTO.getEmissionTradingScheme());
    }

}
