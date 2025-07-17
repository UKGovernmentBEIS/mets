package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class PermitRevocationWithdrawnDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitRevocationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_WITHDRAWN;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitRevocationRequestPayload payload, String requestId) {
    	return Map.of(
    			"withdrawCompletedDate", Date.from(payload.getWithdrawCompletedDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
    			);
    }

}
