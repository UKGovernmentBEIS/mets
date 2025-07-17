package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@Component
@RequiredArgsConstructor
public class RdeSubmitDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<RequestPayloadRdeable> {

    private final RequestRepository requestRepository;
    private final AccountQueryService accountQueryService;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.RDE_SUBMIT;
    }
    
    @Override
    public Map<String, Object> constructParams(RequestPayloadRdeable payload, String requestId) {
        
        final Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        final EmissionTradingScheme accountEmissionTradingScheme =
            accountQueryService.getAccountEmissionTradingScheme(request.getAccountId());
        final boolean isCorsia = accountEmissionTradingScheme == EmissionTradingScheme.CORSIA;
        
        return Map.of(
                "extensionDate", Date.from(payload.getRdeData().getRdePayload().getExtensionDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "deadline", Date.from(payload.getRdeData().getRdePayload().getDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "isCorsia", isCorsia
                );
    }

}

