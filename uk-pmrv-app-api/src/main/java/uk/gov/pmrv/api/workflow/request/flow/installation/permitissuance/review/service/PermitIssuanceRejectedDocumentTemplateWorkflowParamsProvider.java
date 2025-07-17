package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

import java.util.Map;

@Component
public class PermitIssuanceRejectedDocumentTemplateWorkflowParamsProvider
        implements DocumentTemplateWorkflowParamsProvider<PermitIssuanceRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(PermitIssuanceRequestPayload payload, String requestId) {
        PermitIssuanceRejectDetermination determination = (PermitIssuanceRejectDetermination) payload.getDetermination();
        return Map.of("officialRefusalLetter", determination.getOfficialNotice());
    }
}
