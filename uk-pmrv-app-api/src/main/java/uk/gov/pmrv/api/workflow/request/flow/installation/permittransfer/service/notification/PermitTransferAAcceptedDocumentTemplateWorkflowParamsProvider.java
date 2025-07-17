package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitTransferAAcceptedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitTransferARequestPayload> {
    
    private final RequestService requestService;
    private final PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider commonProvider;
    
    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_ACCEPTED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitTransferARequestPayload payload, String requestId) {
        
        final String receiverRequestId = payload.getRelatedRequestId();
        final Request receiverRequest = requestService.findRequestById(receiverRequestId);
        final String transfererRequestId = ((PermitTransferBRequestPayload) receiverRequest.getPayload()).getRelatedRequestId();
        final Request transfererRequest = requestService.findRequestById(transfererRequestId);

        return commonProvider.constructParams(receiverRequest, transfererRequest);
    }

}
