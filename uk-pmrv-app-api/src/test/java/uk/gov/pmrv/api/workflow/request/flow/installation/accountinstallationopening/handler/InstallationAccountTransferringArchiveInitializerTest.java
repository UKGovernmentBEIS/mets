package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountTransferringArchiveRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountTransferringArchiveInitializerTest {

    @InjectMocks
    private InstallationAccountTransferringArchiveInitializer initializer;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void initializePayload() {

        final String requestId = "1";
        final long accountId = 2L;
        final String transferCode = "123456789";

        final InstallationAccountOpeningRequestPayload installationAccountOpeningRequestPayload =
            InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountPayload(new InstallationAccountPayload())
                .build();
        
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .status(RequestStatus.IN_PROGRESS)
            .payload(installationAccountOpeningRequestPayload)
            .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(accountId))
            .thenReturn(InstallationAccountInfoDTO.builder().transferCode(transferCode).build());
        
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(InstallationAccountTransferringArchiveRequestTaskPayload.class);
        assertThat(((InstallationAccountTransferringArchiveRequestTaskPayload)requestTaskPayload).getTransferCode()).isEqualTo(transferCode);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE);
    }
}
