package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitTransferBRefusedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitTransferBRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_REFUSED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitTransferBRequestPayload payload, String requestId) {

        final Map<String, Object> params = new HashMap<>();

        final PermitIssuanceRejectDetermination determination =
            (PermitIssuanceRejectDetermination) payload.getDetermination();
        final String officialNotice = determination.getOfficialNotice();

        params.put("officialNotice", officialNotice);

        return params;
    }

}
