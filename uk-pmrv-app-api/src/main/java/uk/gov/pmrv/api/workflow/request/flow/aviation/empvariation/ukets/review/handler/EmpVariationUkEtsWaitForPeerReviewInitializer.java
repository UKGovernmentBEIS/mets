package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper.EmpVariationUkEtsReviewMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

	private static final EmpVariationUkEtsReviewMapper EMP_VARIATION_UKETS_MAPPER = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    
    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        final RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        
        return EMP_VARIATION_UKETS_MAPPER.toEmpVariationUkEtsApplicationReviewRequestTaskPayload(
                requestPayload, accountInfo, RequestTaskPayloadType.EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD
            );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW);
    }
}
