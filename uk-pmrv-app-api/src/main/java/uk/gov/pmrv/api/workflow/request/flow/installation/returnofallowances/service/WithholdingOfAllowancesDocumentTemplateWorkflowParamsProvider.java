package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReasonType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
public class WithholdingOfAllowancesDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<WithholdingOfAllowancesRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.WITHHOLDING_OF_ALLOWANCES;
    }

    @Override
    public Map<String, Object> constructParams(WithholdingOfAllowancesRequestPayload payload, String requestId) {
        Map<String, Object> params = new HashMap<>(Map.of(
            "reasonType", payload.getWithholdingOfAllowances().getReasonType(),
            "year", payload.getWithholdingOfAllowances().getYear()
        ));
        if (payload.getWithholdingOfAllowances().getReasonType() == WithholdingOfAllowancesReasonType.OTHER) {
            params.put("otherReason", payload.getWithholdingOfAllowances().getOtherReason());
        }
        return params;
    }
}
