package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitTransferAGrantedService {

    private final RequestService requestService;
    private final InstallationAccountUpdateService installationAccountUpdateService;

    public void grant(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();

        installationAccountUpdateService.updateAccountUponTransferAGranted(accountId);
    }
}
