package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class AviationVirReviewedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<AviationVirRequestPayload> {

    private final RequestRepository requestRepository;
    private final AccountQueryService accountQueryService;
    
    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AVIATION_VIR_REVIEWED;
    }

    @Override
    public Map<String, Object> constructParams(AviationVirRequestPayload payload, String requestId) {

        final Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        final EmissionTradingScheme accountEmissionTradingScheme = 
            accountQueryService.getAccountEmissionTradingScheme(request.getAccountId());
        final boolean isCorsia = accountEmissionTradingScheme == EmissionTradingScheme.CORSIA;

        return Map.of(
            "regulatorReviewResponse", payload.getRegulatorReviewResponse(),
            "isCorsia", isCorsia
        );
    }
}
