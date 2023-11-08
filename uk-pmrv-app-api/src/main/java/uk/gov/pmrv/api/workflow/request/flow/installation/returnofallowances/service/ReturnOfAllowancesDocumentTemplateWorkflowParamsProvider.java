package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReturnOfAllowancesDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<ReturnOfAllowancesRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.RETURN_OF_ALLOWANCES;
    }

    @Override
    public Map<String, Object> constructParams(ReturnOfAllowancesRequestPayload payload, String requestId) {
        return Map.of(
            "numberOfAllowancesToBeReturned", payload.getReturnOfAllowances().getNumberOfAllowancesToBeReturned(),
            "returnOfAllowancesYears", payload.getReturnOfAllowances().getYears().stream().map(Object::toString)
                .collect(Collectors.joining(",")),
            "returnOfAllowancesReason", payload.getReturnOfAllowances().getReason(),
            "dateToBeReturned", payload.getReturnOfAllowances().getDateToBeReturned()
        );
    }
}
