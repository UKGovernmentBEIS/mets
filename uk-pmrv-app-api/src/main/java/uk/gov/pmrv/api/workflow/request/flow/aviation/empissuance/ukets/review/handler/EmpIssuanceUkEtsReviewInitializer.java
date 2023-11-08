package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper.EmpUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public abstract class EmpIssuanceUkEtsReviewInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpUkEtsReviewMapper EMP_UKETS_REVIEW_MAPPER = Mappers.getMapper(EmpUkEtsReviewMapper.class);

    protected abstract RequestTaskPayloadType getRequestTaskPayloadType();

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return EMP_UKETS_REVIEW_MAPPER.toEmpIssuanceUkEtsApplicationReviewRequestTaskPayload(
            (EmpIssuanceUkEtsRequestPayload)request.getPayload(),
            aviationAccountInfo,
            getRequestTaskPayloadType()
        );
    }

}
