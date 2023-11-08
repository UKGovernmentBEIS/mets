package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceDeemedWithdrawnService {

    private final RequestService requestService;
    private final InstallationAccountStatusService installationAccountStatusService;

    public void deemWithdrawn(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        installationAccountStatusService.handlePermitDeemedWithdrawn(accountId);
    }
}
