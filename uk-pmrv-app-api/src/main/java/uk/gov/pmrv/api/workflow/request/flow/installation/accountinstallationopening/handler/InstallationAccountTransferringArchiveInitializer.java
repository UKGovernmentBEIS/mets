package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountTransferringArchiveRequestTaskPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class InstallationAccountTransferringArchiveInitializer implements InitializeRequestTaskHandler {

    private final InstallationAccountQueryService installationAccountQueryService;
    
    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final String transferCode =
            installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId()).getTransferCode();
        return InstallationAccountTransferringArchiveRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE_PAYLOAD)
            .transferCode(transferCode)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE);
    }
}
