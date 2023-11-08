package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper.EmpCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@Service
@RequiredArgsConstructor
public abstract class EmpIssuanceCorsiaReviewInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpCorsiaReviewMapper EMP_CORSIA_REVIEW_MAPPER = Mappers.getMapper(EmpCorsiaReviewMapper.class);

    protected abstract RequestTaskPayloadType getRequestTaskPayloadType();

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return EMP_CORSIA_REVIEW_MAPPER.toEmpIssuanceCorsiaApplicationReviewRequestTaskPayload(
            (EmpIssuanceCorsiaRequestPayload) request.getPayload(),
            aviationAccountInfo,
            getRequestTaskPayloadType()
        );
    }
}
