package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final PermitQueryService permitQueryService;
    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    private final PermitIdentifierGenerator generator;
    
    public Map<String, Object> constructParams(final Request receiverRequest,
                                               final Request transfererRequest) {

        final Map<String, Object> params = new HashMap<>();

        final Long transfererAccountId = transfererRequest.getAccountId();
        final InstallationAccountWithoutLeHoldingCompanyDTO transfererAccount = installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(transfererAccountId);
        final String transfererLeName = transfererAccount.getLegalEntity().getName();
        final String transfererInstallationName = transfererAccount.getName();
        final String transfererPermitId = permitQueryService.getPermitIdByAccountId(transfererAccountId).orElse(null);
        final String transfererInstallationAddress = documentTemplateLocationInfoResolver.constructAddressInfo(transfererAccount.getLegalEntity().getAddress());

        final Long receiverAccountId = receiverRequest.getAccountId();
        final InstallationAccountWithoutLeHoldingCompanyDTO receiverAccount = installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(receiverAccountId);
        final String receiverLeName = receiverAccount.getLegalEntity().getName();
        final String receiverPermitId = permitQueryService.getPermitIdByAccountId(receiverAccountId).orElse(generator.generate(receiverAccountId));
        final PermitTransferBRequestPayload receiverRequestPayload = (PermitTransferBRequestPayload) receiverRequest.getPayload();
        //on pdf preview, receiverRequestPayloadRequestPayload.determination Object has not been set yet, so a null check is required.
        final LocalDate transferDate = receiverRequestPayload.getDetermination() != null ? ((PermitIssuanceGrantDetermination) receiverRequestPayload.getDetermination()).getActivationDate() : null;

        params.put("transfererPermitId", transfererPermitId);
        params.put("transferer", transfererLeName);
        params.put("transfererInstallationName", transfererInstallationName);
        params.put("transfererInstallationAddress", transfererInstallationAddress);
        params.put("receiver", receiverLeName);
        params.put("receiverPermitId", receiverPermitId);
        if (transferDate != null) {
            params.put("transferDate", Date.from(transferDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        return params;
    }
}
