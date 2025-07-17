package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

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
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;

@Component
@RequiredArgsConstructor
public class RfiSubmitDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<RequestPayloadRfiable> {

    private final RequestRepository requestRepository;
    private final AccountQueryService accountQueryService;

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.RFI_SUBMIT;
    }

    @Override
    public Map<String, Object> constructParams(RequestPayloadRfiable payload, String requestId) {

        final Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        final EmissionTradingScheme accountEmissionTradingScheme =
            accountQueryService.getAccountEmissionTradingScheme(request.getAccountId());
        final boolean isCorsia = accountEmissionTradingScheme == EmissionTradingScheme.CORSIA;
        
        return Map.of(
                "deadline", Date.from(payload.getRfiData().getRfiDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "questions", payload.getRfiData().getRfiQuestionPayload().getQuestions(),
                "isCorsia", isCorsia
                );
    }

}
