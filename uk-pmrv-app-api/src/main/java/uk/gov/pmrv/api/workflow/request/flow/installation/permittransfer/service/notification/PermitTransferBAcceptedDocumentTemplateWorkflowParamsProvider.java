package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@Component
@RequiredArgsConstructor
public class PermitTransferBAcceptedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitTransferBRequestPayload> {
    
    private final RequestService requestService;
    private final PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider commonProvider;
    
    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_ACCEPTED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitTransferBRequestPayload payload, String requestId) {

        final String transfererRequestId = payload.getRelatedRequestId();
        final Request transfererRequest = requestService.findRequestById(transfererRequestId);
        final String receiverRequestId = ((PermitTransferARequestPayload) transfererRequest.getPayload()).getRelatedRequestId();
        final Request receiverRequest = requestService.findRequestById(receiverRequestId);

        return commonProvider.constructParams(receiverRequest, transfererRequest);
    }

}
