package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountActivationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningActivateAccountService {
    private final InstallationAccountPayloadMapper installationAccountPayloadMapper;
    private final InstallationAccountActivationService installationAccountActivationService;
    private final RequestService requestService;

    public void execute(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload =
            (InstallationAccountOpeningRequestPayload) request.getPayload();
        InstallationAccountDTO accountDTO = installationAccountPayloadMapper.toAccountInstallationDTO(instAccOpeningRequestPayload.getAccountPayload());
        installationAccountActivationService.activateAccount(request.getAccountId(), accountDTO, instAccOpeningRequestPayload.getOperatorAssignee());
    }
}
