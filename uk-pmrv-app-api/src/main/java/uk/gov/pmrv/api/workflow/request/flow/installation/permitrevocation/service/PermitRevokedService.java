package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitRevokedService {

    private final RequestService requestService;
    private final InstallationAccountStatusService installationAccountStatusService;

    public void executePermitRevokedPostActions(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();

        // update account status
        installationAccountStatusService.handlePermitRevoked(accountId);
    }

    public Map<String, Object> getAerVariables(String requestId) {
        Map<String, Object> variables = new HashMap<>();

        Request request = requestService.findRequestById(requestId);
        PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        PermitRevocation revocation = requestPayload.getPermitRevocation();

        variables.put(BpmnProcessConstants.AER_REQUIRED, revocation.getAnnualEmissionsReportRequired());

        if(Boolean.TRUE.equals(revocation.getAnnualEmissionsReportRequired())){
            variables.put(BpmnProcessConstants.AER_EXPIRATION_DATE,
                    DateUtils.convertLocalDateToDate(revocation.getAnnualEmissionsReportDate()));
        }

        return variables;
    }
}
