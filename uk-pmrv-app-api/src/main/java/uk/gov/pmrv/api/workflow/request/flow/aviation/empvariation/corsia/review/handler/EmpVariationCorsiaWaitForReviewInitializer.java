package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper.EmpVariationCorsiaReviewMapper;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaWaitForReviewInitializer implements InitializeRequestTaskHandler {

    private static final EmpVariationCorsiaReviewMapper EMP_VARIATION_CORSIA_MAPPER = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        return EMP_VARIATION_CORSIA_MAPPER.toEmpVariationCorsiaApplicationReviewRequestTaskPayload(
                requestPayload, accountInfo, RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW);
    }
}