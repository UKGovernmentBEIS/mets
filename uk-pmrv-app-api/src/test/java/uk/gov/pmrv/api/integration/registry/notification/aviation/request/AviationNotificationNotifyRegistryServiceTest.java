package uk.gov.pmrv.api.integration.registry.notification.aviation.request;

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
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.notification.aviation.request.requestaction.AviationNotificationRegistryIntegrationAddRequestActionService;
import uk.gov.pmrv.api.integration.registry.notification.common.RegistryNotificationType;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationNotificationNotifyRegistryServiceTest {

    @Mock
    private AviationNotificationRegistryProducer registryProducer;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private AviationNotificationRegistryIntegrationAddRequestActionService addRequestActionService;

    @InjectMocks
    private AviationNotificationNotifyRegistryService service;

    @Test
    void notifyRegistry_success_with_file_and_account_closure_timeline_action() {
        Long accountId = 1L;
        Integer registryId = 12345;
        String requestId = "req-123";
        String fileUuid = "file-uuid-001";
        String fileName = "aviation_notice.pdf";
        byte[] fileContent = "file content bytes".getBytes();

        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .uuid(fileUuid)
                .name(fileName)
                .build();

        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .fileInfoDTO(fileInfoDTO)
                .registryNotificationType(RegistryNotificationType.AVIATION_ACCOUNT_CLOSED)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        FileDTO fileDTO = FileDTO.builder()
                .fileName(fileName)
                .fileContent(fileContent)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);
        when(fileDocumentService.getFileDTO(fileUuid)).thenReturn(fileDTO);

        service.notifyRegistry(event);

        ArgumentCaptor<RegulatorNoticeEvent> captor = ArgumentCaptor.forClass(RegulatorNoticeEvent.class);
        verify(registryProducer).produce(captor.capture());

        RegulatorNoticeEvent producedEvent = captor.getValue();
        assertEquals(String.valueOf(registryId), producedEvent.getRegistryId());
        assertEquals(fileName, producedEvent.getFileName());
        assertArrayEquals(fileContent, producedEvent.getFileData());
        assertEquals(RegistryNotificationType.AVIATION_ACCOUNT_CLOSED.getName(), producedEvent.getType());

    }

    @Test
    void notifyRegistry_success() {
        Long accountId = 1L;
        Integer registryId = 12345;
        String requestId = "req-123";

        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .accountId(accountId)
                .requestId(requestId)
                .registryNotificationType(RegistryNotificationType.EMP_ISSUANCE_DEEMED_WITHDRAWN_NOTIFICATION)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .registryId(registryId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        ArgumentCaptor<RegulatorNoticeEvent> captor = ArgumentCaptor.forClass(RegulatorNoticeEvent.class);
        verify(registryProducer).produce(captor.capture());

        RegulatorNoticeEvent producedEvent = captor.getValue();
        assertEquals(String.valueOf(registryId), producedEvent.getRegistryId());
        assertNull(producedEvent.getFileName());
        assertNull(producedEvent.getFileData());
        assertEquals(RegistryNotificationType.EMP_ISSUANCE_DEEMED_WITHDRAWN_NOTIFICATION.getName(), producedEvent.getType());

        verify(addRequestActionService).addRequestAction(eq(event), eq(registryId));

    }

    @Test
    void notifyRegistry_aborts_when_registry_id_missing() {
        Long accountId = 1L;

        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .accountId(accountId)
                .registryNotificationType(RegistryNotificationType.AVIATION_ACCOUNT_CLOSED)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .registryId(null)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verifyNoInteractions(registryProducer);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(addRequestActionService);
    }

    @Test
    void notifyRegistry_aborts_when_emission_trading_scheme_is_not_uk_ets_aviation() {
        Long accountId = 1L;

        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .accountId(accountId)
                .registryNotificationType(RegistryNotificationType.AVIATION_ACCOUNT_CLOSED)
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .registryId(12345)
                .emissionTradingScheme(EmissionTradingScheme.CORSIA)
                .build();

        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        service.notifyRegistry(event);

        verifyNoInteractions(registryProducer);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(addRequestActionService);
    }
}