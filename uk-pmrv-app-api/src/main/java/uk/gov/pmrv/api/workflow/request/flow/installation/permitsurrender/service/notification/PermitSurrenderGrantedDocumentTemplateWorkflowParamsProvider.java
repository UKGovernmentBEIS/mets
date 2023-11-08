package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;

@Component
public class PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitSurrenderRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitSurrenderRequestPayload payload, String requestId) {
        PermitSurrenderReviewDeterminationGrant grantDetermiation = (PermitSurrenderReviewDeterminationGrant) payload.getReviewDetermination();
        Map<String, Object> params = new HashMap<>();
        params.put("noticeDate", Date.from(grantDetermiation.getNoticeDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        if(grantDetermiation.getReportDate() != null) {
            params.put("reportDate", Date.from(grantDetermiation.getReportDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if(grantDetermiation.getAllowancesSurrenderDate() != null) {
            params.put("allowancesSurrenderDate", Date.from(grantDetermiation.getAllowancesSurrenderDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        return params;
    }

}

