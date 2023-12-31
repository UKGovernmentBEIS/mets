package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountRejectionService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningRejectAccountService {
    private final InstallationAccountRejectionService installationAccountRejectionService;
    private final RequestService requestService;

    public void execute(String requestId) {
    	Request request = requestService.findRequestById(requestId);
		installationAccountRejectionService.rejectAccount(request.getAccountId());
    }
}
