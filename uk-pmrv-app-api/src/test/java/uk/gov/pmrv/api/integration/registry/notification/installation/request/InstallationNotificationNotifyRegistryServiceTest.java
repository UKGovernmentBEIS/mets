package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationNotificationNotifyRegistryServiceTest {

    @Mock
    private InstallationNotificationRegistryProducer registryProducer;

    @Mock
    private InstallationAccountQueryService accountQueryService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private NotificationRegistryIntegrationAddRequestActionService addRequestActionService;

    @Mock
    private RegistryIdEmailNotifierService notifierService;

    @InjectMocks
    private InstallationNotificationNotifyRegistryService service;

    @Test
    void notifyRegistry_success() {
        Long accountId = 1L;
        int registryId = 12345;
        String requestId = "req-123";
        String fileUuid = "file-uuid-001";
        String fileName = "surrender_notice.pdf";
        byte[] fileContent = "file content bytes".getBytes();

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid(fileUuid)
                .name(fileName)
                .build();

        NotificationRegistryEvent event = NotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .fileInfoDTO(fileInfoDTO)
                .registryNotificationType(RegistryNotificationType.SURRENDER_NOTIFICATION)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emitterType(EmitterType.GHGE)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();

        FileDTO fileDTO = FileDTO.builder()
                .fileContent(fileContent)
                .fileName(fileName)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);
        when(fileDocumentService.getFileDTO(fileUuid)).thenReturn(fileDTO);

        service.notifyRegistry(event);

        ArgumentCaptor<RegulatorNoticeEvent> captor = ArgumentCaptor.forClass(RegulatorNoticeEvent.class);
        verify(registryProducer).produce(captor.capture());

        RegulatorNoticeEvent producedEvent = captor.getValue();
        assertEquals(String.valueOf(registryId), producedEvent.getRegistryId());
        assertEquals(fileName, producedEvent.getFileName());
        assertArrayEquals(fileContent, producedEvent.getFileData());

        verify(addRequestActionService).addRequestAction(eq(requestId), eq(producedEvent), eq(fileInfoDTO));
        verifyNoInteractions(notifierService);
    }

    @Test
    void notifyRegistry_aborts_when_registry_id_missing() {
        Long accountId = 1L;
        String requestId = "req-123";
        String fileUuid = "file-uuid-001";
        String fileName = "surrender_notice.pdf";

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid(fileUuid)
                .name(fileName)
                .build();

        NotificationRegistryEvent event = NotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .fileInfoDTO(fileInfoDTO)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(null)
                .emitterType(EmitterType.GHGE)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verify(notifierService).registryIdNonExistenceNotifyRegulator(
                eq(accountDTO),
                eq(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_NOTIFICATION_MISSING_REGISTRY_ID.getName()),
                eq(NotifyRegistryUtils.INSTALLATION_SERVICE_KEY));
        verifyNoInteractions(registryProducer);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(addRequestActionService);
    }

    @Test
    void notifyRegistry_aborts_when_emitter_type_is_not_ghge() {
        Long accountId = 1L;
        int registryId = 12345;
        String requestId = "req-123";
        String fileUuid = "file-uuid-001";
        String fileName = "surrender_notice.pdf";

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid(fileUuid)
                .name(fileName)
                .build();

        NotificationRegistryEvent event = NotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .fileInfoDTO(fileInfoDTO)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emitterType(EmitterType.HSE)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verifyNoInteractions(registryProducer);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(addRequestActionService);
        verifyNoInteractions(notifierService);
    }

    @Test
    void notifyRegistry_aborts_when_emission_trading_scheme_is_not_uk_ets_installations() {
        Long accountId = 1L;
        int registryId = 12345;
        String requestId = "req-123";
        String fileUuid = "file-uuid-001";
        String fileName = "surrender_notice.pdf";

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid(fileUuid)
                .name(fileName)
                .build();

        NotificationRegistryEvent event = NotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .fileInfoDTO(fileInfoDTO)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emitterType(EmitterType.GHGE)
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .build();

        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verifyNoInteractions(registryProducer);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(addRequestActionService);
        verifyNoInteractions(notifierService);
    }
}

