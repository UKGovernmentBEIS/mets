package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitTransferARefusedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitTransferARequestPayload> {
    
    private final RequestService requestService;
    
    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_REFUSED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitTransferARequestPayload payload, String requestId) {

        final Map<String, Object> params = new HashMap<>();

        final String receiverRequestId = payload.getRelatedRequestId();
        final Request receiverRequest = requestService.findRequestById(receiverRequestId);
        final PermitTransferBRequestPayload receiverRequestPayload = (PermitTransferBRequestPayload) receiverRequest.getPayload();

        final PermitIssuanceRejectDetermination determination =
            (PermitIssuanceRejectDetermination) receiverRequestPayload.getDetermination();
        final String officialNotice = determination.getOfficialNotice();

        params.put("officialNotice", officialNotice);

        return params;
    }

}
